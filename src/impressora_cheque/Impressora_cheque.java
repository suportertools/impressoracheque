/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impressora_cheque;

import com.sun.jna.Native;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;
import utils.AnaliseString;
import utils.WSImpressoraCheque;

/**
 *
 * @author Claudemir Rtools
 */
public class Impressora_cheque {

    private static final GravaConfiguracao GC = new GravaConfiguracao();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (!GC.conf()) {
            return;
        }
        try {
            if (!impressora_ativa()) {
                JOptionPane.showMessageDialog(null, "Erro ao Iniciar Impressora!");
                return;
            }
            
            GC.criar_SystemTray();
            
            Boolean parar = false;
            
            JOptionPane.showMessageDialog(null, "Programa em Funcionamento.");
            while (!parar) {
                StringBuilder response = new StringBuilder();
                //URL url = new URL("http://localhost:8084/Sindical/ws/Sindical/verifica_impressora/1");
                URL url = new URL(GC.getField_caminho().getText() + "/verifica_impressora/" + GC.getField_impressora().getText());
                Charset charset = Charset.forName("UTF8");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
                con.setRequestMethod("GET");
                con.connect();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                    response.append(in.readLine());
                    in.close();
                }
                con.disconnect();
                JSONObject jSONObject = new JSONObject(response.toString());

                if (jSONObject.getInt("id") != -1) {
                    GC.getLabel_ativa().setText("IMPRIMINDO DOCUMENTO...");
                    WSImpressoraCheque ic = new WSImpressoraCheque(
                            jSONObject.getInt("id"),
                            jSONObject.getString("apelido"),
                            jSONObject.getBoolean("ativo"),
                            jSONObject.getString("banco"),
                            jSONObject.getString("valor"),
                            AnaliseString.normalizeUpper(jSONObject.getString("favorecido")),
                            AnaliseString.normalizeUpper(jSONObject.getString("cidade")),
                            jSONObject.getString("data"),
                            jSONObject.getString("mensagem")
                    );
                    
                    Bematech lib = (Bematech) Native.loadLibrary("BEMADP32", Bematech.class);
                    
                    if (erro(lib.IniciaPorta("COM2"))) {
                        impressora_limpa("Não foi possível abrir porta da Impressora");
                        Thread.sleep(1000);
                        continue;
                    }

                    if (erro(lib.ImprimeCheque(ic.getBanco(), ic.getValor(), ic.getFavorecido(), ic.getCidade(), ic.getData(), ""))) {
                        impressora_limpa("Não foi possível imprimir Cheque");
                        Thread.sleep(1000);
                        continue;
                    }

                    if (erro(lib.FechaPorta("COM2"))) {
                        impressora_limpa("Não foi possível fechar porta da Impressora");
                        Thread.sleep(1000);
                        continue;
                    }

                    Thread.sleep(6000);
                    GC.getLabel_ativa().setText("ATIVA");
                    impressora_limpa("ok");
                }

                Thread.sleep(1000);

                if (!impressora_ativa()) {
                    JOptionPane.showMessageDialog(null, "Sistema sindical fora do ar.");
                    System.exit(0);
                    return;
                }
            }

        } catch (IOException | JSONException | InterruptedException e) {
            e.getMessage();
        }
        //WSImpressoraCheque ic = new WSImpressoraCheque(0, Integer.BYTES, apelido, Boolean.TRUE, banco, valor, favorecido, cidade, data, mensagem)
//        

    }

    public static boolean impressora_ativa() {
        try {
            //URL url = new URL("http://localhost:8084/Sindical/ws/Sindical/ativa_impressora/1/true");
            URL url = new URL(GC.getField_caminho().getText() + "/ativa_impressora/" + GC.getField_impressora().getText() + "/true");

            Charset charset = Charset.forName("UTF8");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
            con.setRequestMethod("GET");
            con.connect();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                in.close();
            }
            con.disconnect();
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
        return true;
    }

    public static boolean impressora_limpa(String mensagem) {
        try {
            mensagem = mensagem.replace(" ", "%20");
            //URL url = new URL("http://localhost:8084/Sindical/ws/Sindical/limpa_impressora/1/" + mensagem);
            URL url = new URL(GC.getField_caminho().getText() + "/limpa_impressora/" + GC.getField_impressora().getText() + "/" + mensagem);
            Charset charset = Charset.forName("UTF8");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
            con.setRequestMethod("GET");
            con.connect();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                in.close();
            }
            con.disconnect();
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
        return true;
    }

    public static boolean erro(Integer retorno) {
        switch (retorno) {
            case 0:
                System.out.println("Erro de Comunicação com a Impressora. Verifique.");
                return true;
            case 1:
                System.out.println("Comando enviado com Sucesso.");
                return false;
            case -2:
                System.out.println("Parâmetro de Comando Inválido. Verifique.");
                return true;
            case -3:
                System.out.println("Banco Não Encontrado.");
                return true;
            default:
                System.out.println("Erro não Encontrado.");
        }
        return false;
    }

}
