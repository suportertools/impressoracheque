/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impressora_cheque;

import com.sun.jna.win32.StdCallLibrary;

/**
 *
 * @author Claudemir Rtools
 */
public interface Bematech extends StdCallLibrary {

    int IniciaPorta(String porta);

    int FechaPorta(String porta);

    int FechaPorta();

    int ImprimeCheque(String banco, String valor, String favorecido, String cidade, String data, String mensagem);

    int ImprimeChequeTransferencia(String banco, String valor, String cidade, String data, String agencia, String conta, String mensagem);

    int ImprimeCopiaCheque();

    /**
     * @param trava
     * @return int
     * @see "1 para travar"
     * @see "0 para destravar"
     */
    int TravaDocumento(int trava);

    int ImprimeTexto(String texto, int AvancaLinha);

    int FormataTexto(String texto, String letra, int italico, int expandido, int negrito);

    int IncluiCidadeFavorecido(String cidade, String favorecido);

    int IncluiAlteraBanco(String banco, String coordenadas);

    int IncluiAlteraBancoTransferencia(String banco, String coordenadas);

    int ProgramaMoeda(String MoedaSingular, String MoedaPlural);

    int ProgramaFavorecido(String codiFavorecido, String NomeFavorecido);

    int DesprogramaFavorecido(String codiFavorecido);

    int ProgramaBanco(String banco, String coordenadas);

    int ExcluiBanco(String banco);

    int ExcluiBancoTransferencia(String banco);

    int EnviaComando(String comando, int tamanho);

    int ConfiguraImpressora(int linhaChancela, int preenchimento, int velocidade, int numeroBits, int paridade, int centavos);

    int CancelaRelatorio(String opcao);

    int ConfiguraModeloImpressora(int modeloImpressora);

    int ReinicializaConfiguracao();
}
