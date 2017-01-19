/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impressora_cheque;

import com.sun.jna.Native;
import javax.swing.JOptionPane;
import utils.Block;
import utils.BlockInterface;
import utils.WSImpressoraCheque;

/**
 *
 * @author Claudemir Rtools
 */
public class Test {

    private static final GravaConfiguracao GC = new GravaConfiguracao();
    private static Integer errors = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
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
        /*
        try {
            Native.loadLibrary("BEMADP32", Bematech.class);
        } catch (UnsatisfiedLinkError | Exception aaa) {
            JOptionPane.showMessageDialog(null, "ERRO DE DLL:" + aaa.getMessage());
            System.exit(0);
            return;
        }*/

        GC.criar_SystemTray();
        WSImpressoraCheque ic = new WSImpressoraCheque(
                1,
                "TESTE",
                true,
                "001",
                "100.00",
                "SERGINHO GROISMAN",
                "SAO PAULO",
                "01/01/2017",
                "DEVEDOR",
                ""
        );
        Bematech lib;
        try {
            lib = (Bematech) Native.loadLibrary("BEMADP32", Bematech.class);
        } catch (UnsatisfiedLinkError | Exception aaa) {
            JOptionPane.showMessageDialog(null, "ERRO DE DLL:" + aaa.getMessage());
            System.exit(0);
            return;
        }
        if (erro(lib.IniciaPorta(GC.getField_port().getText()))) {

            Thread.sleep(1000);
            return;
        }
        if (erro(lib.ImprimeCheque(ic.getBanco(), ic.getValor(), ic.getFavorecido(), ic.getCidade(), ic.getData(), ""))) {

            Thread.sleep(1000);
            return;
        }

        if (erro(lib.FechaPorta(GC.getField_port().getText()))) {

            Thread.sleep(1000);
            return;
        }

        //WSImpressoraCheque ic = new WSImpressoraCheque(0, Integer.BYTES, apelido, Boolean.TRUE, banco, valor, favorecido, cidade, data, mensagem)
//        
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
