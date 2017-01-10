/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impressora_cheque;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Claudemir Rtools
 */
public class GravaConfiguracao {

    private JTextField field_caminho = new JTextField("");
    private JTextField field_impressora = new JTextField("");
    private JTextField field_port = new JTextField("");
    private JLabel label_ativa = new JLabel("ATIVA");
    
    public GravaConfiguracao() {
        //field_impressora.setEnabled(false);

    }

    public ActionListener Action_Tray() {
        ActionListener action_tray = (ActionEvent e) -> {
            JFrame frame = new JFrame();
            frame.setTitle("Impressora de Cheques");
            frame.setAutoRequestFocus(true);
            frame.setLayout(null);
            frame.setBounds(0, 0, 450, 180);
            frame.setResizable(false);
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // centralizar frame
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

            JButton button_esconder = new JButton("Esconder");
            button_esconder.addActionListener(Action_Esconder(frame));
            button_esconder.setBounds(10, 100, 200, 30);

            JButton button_sair = new JButton("Sair do Sistema");
            button_sair.addActionListener(Action_Sair());
            button_sair.setBounds(220, 100, 200, 30);

            JLabel label_caminho = new JLabel(field_caminho.getText());
            label_caminho.setBounds(10, 10, 800, 30);

            JLabel label_impressora = new JLabel("Impressora: " + field_impressora.getText() + " - PORTA: " + field_port.getText());
            label_impressora.setBounds(10, 30, 800, 30);

            //JLabel label_ativa = new JLabel("ATIVA");
            label_ativa.setBounds(10, 50, 500, 50);
            label_ativa.setFont(new Font(null, Font.PLAIN, 20));

            frame.add(button_esconder);
            frame.add(button_sair);
            frame.add(label_caminho);
            frame.add(label_impressora);
            frame.add(label_ativa);

            frame.setVisible(true);
        };
        return action_tray;
    }

    public ActionListener Action_Sair() {
        ActionListener action_sair = (ActionEvent e) -> {
            System.exit(0);
        };
        return action_sair;
    }

    public ActionListener Action_Esconder(JFrame frame) {
        ActionListener action_esconder = (ActionEvent e) -> {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        };
        return action_esconder;
    }

    public void criar_SystemTray(){
        try {
            //PopupMenu popupMenu = new PopupMenu();

            SystemTray tray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(new ImageIcon("C:/rtools/impressora/impressora-icon.png").getImage(), "Impressora de Cheques");

            trayIcon.addActionListener(Action_Tray());
            //trayIcon.setPopupMenu(popupMenu);

            tray.add(trayIcon);
        } catch (HeadlessException | AWTException e) {
            e.getMessage();
        }
    }
    
    public Boolean conf() {
        File file = new File("C:/rtools/impressora");
        file.mkdirs();
        
        File file_exists = new File("C:/rtools/impressora/conf.json");
        if (file_exists.exists()) {
            String str_json;
            try {
                str_json = new String(Files.readAllBytes(Paths.get(file_exists.getPath())));
                JSONObject json = new JSONObject(str_json);

                field_caminho.setText(json.getString("caminho"));
                field_impressora.setText(json.getString("impressora"));
                field_port.setText(json.getString("port"));

            } catch (IOException | JSONException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                return false;
            }
            return true;
        }

        //String[] items = (String[]) lista_impressora().toArray();//{"One", "Two", "Three", "Four", "Five"};
        //JComboBox combo = new JComboBox(items);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        //panel.add(new JLabel("Impressoras"));
        //panel.add(combo);

        //String[] split = combo.getSelectedItem().toString().split("-");
        //field_impressora.setText(split[0].trim());
        panel.add(new JLabel("Caminho do Sistema"));
        panel.add(field_caminho);
        panel.add(new JLabel("Número da Impressora"));
        panel.add(field_impressora);
        panel.add(new JLabel("Porta"));
        panel.add(field_port);

        int result = JOptionPane.showConfirmDialog(null, panel, "Salvar Configuração", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {

            if (field_caminho.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Digite um caminho válido para impressora funcionar!");
                return false;
            }

            if (field_impressora.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Digite um número para impressora!");
                return false;
            }

            try {
                JSONObject json = new JSONObject();

                json.put("caminho", field_caminho.getText());
                json.put("impressora", field_impressora.getText());

                try (FileWriter file_writer = new FileWriter("C:/rtools/impressora/conf.json")) {
                    file_writer.write(json.toString());
                    file_writer.flush();
                }
                JOptionPane.showMessageDialog(null, "Arquivo salvo em: C:/rtools/impressora/conf.json");
            } catch (JSONException | IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                return false;
            }
//            System.out.println(combo.getSelectedItem()
//                    + " " + field1.getText()
//                    + " " + field2.getText());
        } else {
            JOptionPane.showMessageDialog(null, "A impressora deve ser configurada para funcionar!");
            return false;
        }
        return true;
    }

    public boolean loadJson() {
        return true;
    }

    public List<String> lista_impressora() {
        try {
            //URL url = new URL("http://localhost:8084/Sindical/ws/Sindical/ativa_impressora/1/true");
            // antes de carregar a lista tem que carregar o caminho do sistema
            URL url = new URL(field_caminho.getText() + "/lista_impressora");

            Charset charset = Charset.forName("UTF8");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
            con.setRequestMethod("GET");
            con.connect();

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                response.append(in.readLine());
                in.close();
            }
            con.disconnect();

            JSONArray list = new JSONArray(response.toString());

            List<String> list_ret = new ArrayList();

            for (int i = 0; i < list.length(); i++) {
                list_ret.add(list.getString(i));
            }

            return list_ret;
        } catch (IOException | JSONException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar lista de Impressoras do Sindical: " + e.getMessage());
            return new ArrayList();
        }
    }

    public JTextField getField_caminho() {
        return field_caminho;
    }

    public void setField_caminho(JTextField field_caminho) {
        this.field_caminho = field_caminho;
    }

    public JTextField getField_impressora() {
        return field_impressora;
    }

    public void setField_impressora(JTextField field_impressora) {
        this.field_impressora = field_impressora;
    }

    public JLabel getLabel_ativa() {
        return label_ativa;
    }

    public void setLabel_ativa(JLabel label_ativa) {
        this.label_ativa = label_ativa;
    }

    public JTextField getField_port() {
        return field_port;
    }

    public void setField_port(JTextField field_port) {
        this.field_port = field_port;
    }
}
