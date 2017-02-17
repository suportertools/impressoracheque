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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;
import utils.AnaliseString;
import utils.Block;
import utils.BlockInterface;
import utils.Mac;
import utils.Preloader;
import utils.WSImpressoraCheque;

/**
 *
 * @author Claudemir Rtools
 */
public class Index {

    private static final GravaConfiguracao GC = new GravaConfiguracao();
    private static Integer errors = 0;
    // private final Preloader preloader;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Block.TYPE = "IMPRESSORA";
        if (!Block.registerInstance()) {
            // instance already running.
            System.out.println("Another instance of this application is already running.  Exiting.");
            JOptionPane.showMessageDialog(null, "Aplicação já esta em execução");
            System.exit(0);
        }
        Block.setBlockInterface(new BlockInterface() {
            @Override
            public void newInstanceCreated() {
                System.out.println("New instance detected...");
                // this is where your handler code goes...
            }
        });
        if (!GC.conf()) {
            return;
        }
        try {
            Native.loadLibrary("BEMADP32", Bematech.class);
        } catch (UnsatisfiedLinkError | Exception aaa) {
            JOptionPane.showMessageDialog(null, "ERRO DE DLL:" + aaa.getMessage());
            System.exit(0);
            return;
        }
        try {
            Preloader p = new Preloader();
            p.setAppTitle("Dispostivo - " + Mac.getInstance());
            p.setAppStatus("Iniciando...");
            p.setShowIcon(true);
            p.setWaitingStarted(true);
            p.show();
            Thread.sleep(1000);
            p.reloadStatus("Verificando se computador é registrado...");
            Thread.sleep(3000);
            p.hide();
        } catch (Exception e) {

        }
        load();

        //WSImpressoraCheque ic = new WSImpressoraCheque(0, Integer.BYTES, apelido, Boolean.TRUE, banco, valor, favorecido, cidade, data, mensagem)
//        
    }

    public static void load() {
        try {
            if (!impressora_ativa()) {
                JOptionPane.showMessageDialog(null, "Erro ao Iniciar Impressora!");
                System.exit(0);
                return;
            }

            GC.criar_SystemTray();

            Boolean parar = false;

            JOptionPane.showMessageDialog(null, "Programa em Funcionamento.");

            Preloader p = new Preloader();
            while (!parar) {
                Boolean err = false;
                try {
                    StringBuilder response = new StringBuilder();
                    //URL url = new URL("http://localhost:8084/Sindical/ws/Sindical/verifica_impressora/1");
                    URL url = new URL(GC.getField_caminho().getText() + "/verifica_impressora/" + Mac.getInstance());
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
                    if (response.equals("null")) {
                        JOptionPane.showMessageDialog(null, "O SERVIDOR NAO RETORNOU NENHUMA RESPOSTA!");
                        System.exit(0);
                        return;
                    }
                    JSONObject jSONObject = new JSONObject(response.toString());
                    if (jSONObject.getInt("id") != -1 && !jSONObject.getString("banco").isEmpty() && !jSONObject.getString("valor").isEmpty()) {
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
                                jSONObject.getString("mensagem"),
                                ""
                        // jSONObject.getString("mac")
                        );
                        p = new Preloader();
                        p.setAppTitle(ic.getFavorecido() + " no valor de " + ic.getValor());
                        p.setAppStatus("...");
                        p.setShowIcon(true);
                        p.setWaitingStarted(true);
                        p.show();
                        Bematech lib;
                        try {
                            lib = (Bematech) Native.loadLibrary("BEMADP32", Bematech.class);
                        } catch (UnsatisfiedLinkError | Exception aaa) {
                            JOptionPane.showMessageDialog(null, "ERRO DE DLL:" + aaa.getMessage());
                            p.hide();
                            System.exit(0);
                            return;
                        }
                        if (!err) {
                            if (erro(lib.IniciaPorta(GC.getField_port().getText()))) {
                                err = true;
                                impressora_limpa("Não foi possível abrir porta da Impressora");
                                p.reloadStatus("Não foi possível abrir porta da Impressora");
                                Thread.sleep(1000);
                                p.hide();
                                continue;
                            }

                        }
                        if (!err) {
                            if (erro(lib.ImprimeCheque(ic.getBanco(), ic.getValor(), ic.getFavorecido(), ic.getCidade(), ic.getData(), ""))) {
                                lib.FechaPorta(GC.getField_port().getText());
                                err = true;
                                impressora_limpa("Não foi possível imprimir Cheque");
                                p.reloadStatus("Não foi possível imprimir Cheque");
                                Thread.sleep(1000);
                                p.hide();
                                continue;
                            }
                        }

                        if (!err) {
                            if (erro(lib.FechaPorta(GC.getField_port().getText()))) {
                                err = true;
                                impressora_limpa("Não foi possível fechar porta da Impressora");
                                p.reloadStatus("Não foi possível fechar porta da Impressora");
                                Thread.sleep(1000);
                                p.hide();
                                continue;
                            }
                        }
                        GC.getLabel_ativa().setText("ATIVA");
                        impressora_limpa("ok");
                    }

                    Thread.sleep(6000);
                    p.hide();
                    if (!impressora_ativa()) {
                        p = new Preloader();
                        p.setAppTitle("Servidor offline, aguarde");
                        p.setAppStatus("Servidor offline, aguarde");
                        p.setWaitingStarted(false);
                        p.setMinModal(true);
                        p.show();
                        Thread.sleep(1000);
                        p.hide();
                    }
                } catch (Exception e) {
                    p = new Preloader();
                    p.setAppTitle("Servidor offline, aguarde");
                    p.setAppStatus("Verifique sua conexão com a internet");
                    p.setWaitingStarted(false);
                    p.setMinModal(true);
                    p.show();
                    Thread.sleep(5000);
                    p.hide();
                }
                if (err) {
                    JOptionPane.showMessageDialog(null, "Erro ao executar aplicação. Impressora.");
                    System.exit(0);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao executar aplicação");
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null, "Erro ao executar aplicação");
        System.exit(0);

    }

    public static boolean impressora_ativa() {
        try {
            //URL url = new URL("http://localhost:8084/Sindical/ws/Sindical/ativa_impressora/1/true");
            URL url = new URL(GC.getField_caminho().getText() + "/ativa_impressora/" + Mac.getInstance() + "/true");

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
            URL url = new URL(GC.getField_caminho().getText() + "/limpa_impressora/" + Mac.getInstance() + "/" + mensagem);
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
        return true;
    }

}
