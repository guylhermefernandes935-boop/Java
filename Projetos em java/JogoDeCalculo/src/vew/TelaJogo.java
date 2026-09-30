package vew;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import static vew.TelaJogo.GerenciadorDeFontes.carregarFonte;



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author guylh
 */
public class TelaJogo extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaJogo.class.getName());
    private final Random random = new Random();
    private int max = 10;
    private int min = 1;
    private int contador = 60;
    private Timer timer;
    private int intervalo = 1000; //Normla: 1000, Médio: 500, Dificil: 250, Mortal: 100 
    private final Font fonteBotoes = carregarFonte(20f);
    private int acertos = 0;
    private int erros = 0;
    /**
     * Creates new form TelaJogo
     */
    public TelaJogo() {
        initComponents();
        carregarIcone();
        criandoAcao();
        aplicarFonteEmTudo(jPanel1, fonteBotoes);
        mostraOperacao();
        tempo();        
    }

    private void carregarIcone(){
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/iconeRedusido.png"));
        this.setIconImage(icon.getImage());
    }
    
    private String gerarNumeros(int max, int min){
        
        int primetioNumero = random.nextInt((max - min) + 1) + min;
        int segundoNumero  = random.nextInt((max - min)) + min;
        
        
        return primetioNumero+" , "+segundoNumero;
    }
    
    private void mostraOperacao(){
        int escolhar = random.nextInt((4 - 1)+1) + 1;
        String textMatOriginal = gerarNumeros(max, min);
        String textMatModificado = "";
        
        System.out.println(escolhar);
        switch (escolhar){
            case 1://Adição
                textMatModificado = textMatOriginal.replace(',', '+');
                break;
            case 2://Subritação
                textMatModificado = textMatOriginal.replace(',', '-');
                break;
            case 3://Mutiplicação
                textMatModificado = textMatOriginal.replace(',', 'x');
                break;
            case 4://Divisão
                textMatModificado = textMatOriginal.replace(',', '/');
                break;
        }
        
        operacaoMat.setText(textMatModificado+" = ");
        //System.out.println("Número do texto no começo: "+textMatModificado.length());
        System.out.println("Operação criada");
    }
    
    private void tempo(){
        
        timer = new Timer(intervalo, e -> {
            contador--;
            jblContador.setText(String.valueOf(contador));
            
            if(contador == 0){
                timer.stop();
            }
        });
        timer.start();
    }
    
    private void criandoAcao(){
        //Passa por todos os numeros de 0 a 9
        for(int i = 0; i <= 9; i++){
            final String numero = String.valueOf(i);
            
            Action acaoNumero = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    operacaoMat.setText(operacaoMat.getText()+numero);
                    //System.out.println(numero);
                }
            };
            
            //Mapeira o teclado normal(0-9)
            int codigoTecla = KeyEvent.VK_0 + i;
            operacaoMat.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(codigoTecla, 0), "inserir_"+numero);
            
            //Mapeia o teclado númerico
            int codigoNumpad = KeyEvent.VK_NUMPAD0 + i;
            operacaoMat.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(codigoNumpad, 0), "inserir_"+numero);
            
            operacaoMat.getActionMap().put("inserir_" + numero, acaoNumero);
        }
        
        Action adicionarVigula = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textoFinal = operacaoMat.getText(); 
                
                if(textoFinal.charAt(textoFinal.length() - 1) == ' '){
                    textoFinal = operacaoMat.getText()+"0,";
                }else{
                    textoFinal = operacaoMat.getText()+","; 
                }
                operacaoMat.setText(textoFinal);
            }
        };
        
        operacaoMat.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0),"adicionarVigular_");
        operacaoMat.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0),"adicionarVigular_");
        operacaoMat.getActionMap().put("adicionarVigular_", adicionarVigula);
        
        Action acaoApagar = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                String textoOrigen = operacaoMat.getText();
                String textoFinal;

                if(textoOrigen.endsWith("= ")){
                    return;
                }
                if(textoOrigen.length() > 0){
                    textoFinal = textoOrigen.substring(0, textoOrigen.length() - 1);
                    operacaoMat.setText(textoFinal);
                }
            }
        };
        
        operacaoMat.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "apagar_");
        operacaoMat.getActionMap().put("apagar_", acaoApagar);
        
        Action acaoEnter = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texto = operacaoMat.getText();
                
                String[] partes = texto.split("= ");
                if(partes.length < 2){
                    System.out.println("Nenhuma reposta foi digitada após o '='");
                    return;
                }
                
                String operacao = partes[0].trim();
                String resposta = partes[1].trim();
                String[] valores = operacao.split(" ");
                int num1 = Integer.parseInt(valores[0]);
                char operador = valores[1].charAt(0);
                int num2 = Integer.parseInt(valores[2]);

                double resultado = Double.parseDouble(resposta.replace(",", "."));

                calcularAcertoErro(num1, num2, operador, resultado);
                System.out.println(num1 + " " + operador + " " + num2 + " = " + resultado);
            }
        };
        
        operacaoMat.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "acaoEnter_");
        operacaoMat.getActionMap().put("acaoEnter_", acaoEnter);
    }
    
    private void calcularAcertoErro(int num1, int num2, char operador, double resultado){
        double teste = 0.0;
        
        switch (operador) {
            case '+':
                teste = num1 + num2;
                break;
            case '-': 
                teste = num1 - num2;
                break;
            case 'x':
                teste = num1 * num2;
                break;
            case '/':
                teste = (double) num1 / num2;
                break;
        }
        
        System.out.println(teste);
        
        if(resultado == teste){
            acertos++;
            jlAcertos.setText(acertos+"");
            mostraOperacao();
        } else {
            
            double resultadoArredondado = Math.round(resultado * 1000.0) / 1000.0;
            double testeArredondado = Math.round(teste * 1000.0) / 1000.0;
            if(resultadoArredondado == testeArredondado){
                acertos++;
                jlAcertos.setText(acertos+"");
                mostraOperacao();
            } else{
                erros++;
                jlErros.setText(erros+"");
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        operacaoMat = new javax.swing.JTextField();
        bt1 = new javax.swing.JButton();
        bt2 = new javax.swing.JButton();
        bt3 = new javax.swing.JButton();
        bt4 = new javax.swing.JButton();
        bt5 = new javax.swing.JButton();
        bt6 = new javax.swing.JButton();
        bt7 = new javax.swing.JButton();
        bt8 = new javax.swing.JButton();
        bt9 = new javax.swing.JButton();
        bt0 = new javax.swing.JButton();
        enter = new javax.swing.JButton();
        voltar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jblContador = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jlAcertos = new javax.swing.JLabel();
        jlErros = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jNivle = new javax.swing.JLabel();
        jContadorNivel = new javax.swing.JLabel();
        jbApagar = new javax.swing.JButton();
        btMenos = new javax.swing.JButton();
        btVirgular = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Calculo");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(162, 83, 5));

        operacaoMat.setEditable(false);
        operacaoMat.setBackground(new java.awt.Color(43, 82, 60));
        operacaoMat.setFont(new java.awt.Font("Bradley Hand ITC", 1, 25)); // NOI18N
        operacaoMat.setForeground(new java.awt.Color(255, 255, 255));
        operacaoMat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        operacaoMat.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 5, 5, 5, new java.awt.Color(162, 83, 5)));
        operacaoMat.setFocusable(false);
        operacaoMat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operacaoMatActionPerformed(evt);
            }
        });

        bt1.setBackground(new java.awt.Color(43, 82, 60));
        bt1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt1.setForeground(new java.awt.Color(255, 255, 255));
        bt1.setText("1");
        bt1.setBorder(null);
        bt1.setFocusable(false);
        bt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt1ActionPerformed(evt);
            }
        });
        bt1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                bt1KeyPressed(evt);
            }
        });

        bt2.setBackground(new java.awt.Color(43, 82, 60));
        bt2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt2.setForeground(new java.awt.Color(255, 255, 255));
        bt2.setText("2");
        bt2.setBorder(null);
        bt2.setFocusable(false);
        bt2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt2ActionPerformed(evt);
            }
        });
        bt2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                bt2KeyPressed(evt);
            }
        });

        bt3.setBackground(new java.awt.Color(43, 82, 60));
        bt3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt3.setForeground(new java.awt.Color(255, 255, 255));
        bt3.setText("3");
        bt3.setBorder(null);
        bt3.setFocusable(false);
        bt3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt3ActionPerformed(evt);
            }
        });

        bt4.setBackground(new java.awt.Color(43, 82, 60));
        bt4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt4.setForeground(new java.awt.Color(255, 255, 255));
        bt4.setText("4");
        bt4.setBorder(null);
        bt4.setFocusable(false);
        bt4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt4ActionPerformed(evt);
            }
        });

        bt5.setBackground(new java.awt.Color(43, 82, 60));
        bt5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt5.setForeground(new java.awt.Color(255, 255, 255));
        bt5.setText("5");
        bt5.setBorder(null);
        bt5.setFocusable(false);
        bt5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt5ActionPerformed(evt);
            }
        });

        bt6.setBackground(new java.awt.Color(43, 82, 60));
        bt6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt6.setForeground(new java.awt.Color(255, 255, 255));
        bt6.setText("6");
        bt6.setBorder(null);
        bt6.setFocusable(false);
        bt6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt6ActionPerformed(evt);
            }
        });

        bt7.setBackground(new java.awt.Color(43, 82, 60));
        bt7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt7.setForeground(new java.awt.Color(255, 255, 255));
        bt7.setText("7");
        bt7.setBorder(null);
        bt7.setFocusable(false);
        bt7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt7ActionPerformed(evt);
            }
        });

        bt8.setBackground(new java.awt.Color(43, 82, 60));
        bt8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt8.setForeground(new java.awt.Color(255, 255, 255));
        bt8.setText("8");
        bt8.setBorder(null);
        bt8.setFocusable(false);
        bt8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt8ActionPerformed(evt);
            }
        });

        bt9.setBackground(new java.awt.Color(43, 82, 60));
        bt9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt9.setForeground(new java.awt.Color(255, 255, 255));
        bt9.setText("9");
        bt9.setBorder(null);
        bt9.setFocusable(false);
        bt9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt9ActionPerformed(evt);
            }
        });

        bt0.setBackground(new java.awt.Color(43, 82, 60));
        bt0.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        bt0.setForeground(new java.awt.Color(255, 255, 255));
        bt0.setText("0");
        bt0.setBorder(null);
        bt0.setFocusable(false);
        bt0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt0ActionPerformed(evt);
            }
        });

        enter.setBackground(new java.awt.Color(43, 82, 60));
        enter.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        enter.setForeground(new java.awt.Color(255, 255, 255));
        enter.setText("ENTER");
        enter.setBorder(null);
        enter.setFocusable(false);
        enter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterActionPerformed(evt);
            }
        });

        voltar.setBackground(new java.awt.Color(43, 82, 60));
        voltar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        voltar.setForeground(new java.awt.Color(255, 255, 255));
        voltar.setText("VOLTAR");
        voltar.setBorder(null);
        voltar.setFocusable(false);
        voltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voltarActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(43, 82, 60));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("TEMPO:");

        jblContador.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jblContador.setForeground(new java.awt.Color(255, 51, 51));
        jblContador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jblContador.setText("60");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jblContador, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jblContador)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(43, 82, 60));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 255, 0));
        jLabel1.setText("ACERTOS:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setText("ERROS:");

        jlAcertos.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jlAcertos.setForeground(new java.awt.Color(51, 255, 0));
        jlAcertos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlAcertos.setText("0");

        jlErros.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jlErros.setForeground(new java.awt.Color(255, 102, 102));
        jlErros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlErros.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlAcertos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jlErros, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlAcertos)
                    .addComponent(jlErros))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(43, 82, 60));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jNivle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jNivle.setForeground(new java.awt.Color(255, 255, 255));
        jNivle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jNivle.setText("NIVEL:");

        jContadorNivel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jContadorNivel.setForeground(new java.awt.Color(255, 255, 255));
        jContadorNivel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jContadorNivel.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jContadorNivel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jNivle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jNivle, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jContadorNivel)
                .addGap(19, 19, 19))
        );

        jbApagar.setBackground(new java.awt.Color(43, 82, 60));
        jbApagar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jbApagar.setForeground(new java.awt.Color(255, 255, 255));
        jbApagar.setText("Apagar");
        jbApagar.setBorder(null);
        jbApagar.setFocusable(false);
        jbApagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbApagarActionPerformed(evt);
            }
        });

        btMenos.setBackground(new java.awt.Color(43, 82, 60));
        btMenos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btMenos.setForeground(new java.awt.Color(255, 255, 255));
        btMenos.setText("-");
        btMenos.setBorder(null);
        btMenos.setFocusable(false);
        btMenos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btMenosActionPerformed(evt);
            }
        });

        btVirgular.setBackground(new java.awt.Color(43, 82, 60));
        btVirgular.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btVirgular.setForeground(new java.awt.Color(255, 255, 255));
        btVirgular.setText(",");
        btVirgular.setBorder(null);
        btVirgular.setFocusable(false);
        btVirgular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btVirgularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(bt4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bt5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bt6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(bt1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bt2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bt3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(bt0, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btMenos, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(bt7, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bt8, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(bt9, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btVirgular, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jbApagar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(enter, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(operacaoMat)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(operacaoMat, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bt1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bt5, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bt7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt8, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt9, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bt0, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btMenos, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btVirgular, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(enter, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbApagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bt7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt7ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"7");
    }//GEN-LAST:event_bt7ActionPerformed

    private void operacaoMatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operacaoMatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_operacaoMatActionPerformed

    private void bt1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bt1KeyPressed
    }//GEN-LAST:event_bt1KeyPressed

    private void bt2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bt2KeyPressed

    }//GEN-LAST:event_bt2KeyPressed

    private void bt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt1ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"1");
    }//GEN-LAST:event_bt1ActionPerformed

    private void bt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt2ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"2");
    }//GEN-LAST:event_bt2ActionPerformed

    private void bt3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt3ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"3");
    }//GEN-LAST:event_bt3ActionPerformed

    private void bt4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt4ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"4");
    }//GEN-LAST:event_bt4ActionPerformed

    private void bt5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt5ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"5");
    }//GEN-LAST:event_bt5ActionPerformed

    private void bt6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt6ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"6");
    }//GEN-LAST:event_bt6ActionPerformed

    private void bt8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt8ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"8");
    }//GEN-LAST:event_bt8ActionPerformed

    private void bt9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt9ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"9");
    }//GEN-LAST:event_bt9ActionPerformed

    private void bt0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt0ActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"0");
    }//GEN-LAST:event_bt0ActionPerformed

    private void jbApagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbApagarActionPerformed
        String textoOrigen = operacaoMat.getText();
        String textoFinal;

        if(textoOrigen.endsWith("= ")){
            return;
        }
        if(textoOrigen.length() > 0){
            textoFinal = textoOrigen.substring(0, textoOrigen.length() - 1);
            operacaoMat.setText(textoFinal);
        }

    }//GEN-LAST:event_jbApagarActionPerformed

    private void enterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterActionPerformed
        String texto = operacaoMat.getText();
        
        String[] partes = texto.split("= ");
        if(partes.length < 2){
            System.out.println("Nenhuma reposta foi digitada após o '='");
            return;
        }
        
        String operacao = partes[0].trim();
        String resposta = partes[1].trim();
        String[] valores = operacao.split(" ");
        int num1 = Integer.parseInt(valores[0]);
        char operador = valores[1].charAt(0);
        int num2 = Integer.parseInt(valores[2]);

        double resultado = Double.parseDouble(resposta.replace(",", "."));

        calcularAcertoErro(num1, num2, operador, resultado);
        System.out.println(num1 + " " + operador + " " + num2 + " = " + resultado);
    }//GEN-LAST:event_enterActionPerformed

    private void voltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voltarActionPerformed
        Menu menu = new Menu();
        menu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_voltarActionPerformed

    private void btMenosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btMenosActionPerformed
        operacaoMat.setText(operacaoMat.getText()+"-");
    }//GEN-LAST:event_btMenosActionPerformed

    private void btVirgularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVirgularActionPerformed
        operacaoMat.setText(operacaoMat.getText()+",");
    }//GEN-LAST:event_btVirgularActionPerformed

    public class GerenciadorDeFontes {

        public static Font carregarFonte(float tamanho) {
                try {
                    // Carrega o arquivo de dentro do pacote do projeto (JAR)
                    InputStream is = GerenciadorDeFontes.class.getResourceAsStream("/fonte/Chalk.ttf");
                    if (is == null) {
                        throw new IOException("O arquivo Chalk.ttf não foi encontrado no pacote 'fonte'.");
                    }

                    // Cria a fonte a partir do stream
                    Font fonteBase = Font.createFont(Font.TRUETYPE_FONT, is);

                    // Registra a fonte no sistema gráfico do Java
                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    ge.registerFont(fonteBase);

                    // Retorna a fonte no tamanho desejado
                    return fonteBase.deriveFont(tamanho);

                } catch (FontFormatException | IOException e) {
                    e.printStackTrace();
                    // Caso falhe, retorna uma fonte padrão do sistema para o jogo não quebrar
                    return new Font("Arial", Font.PLAIN, (int) tamanho);
                }
            }
        }

    private void aplicarFonteEmTudo(Container container, Font font){
        Component[] componentes = container.getComponents();
        
        for(Component comp : componentes){
            comp.setFont(fonteBotoes);
            
            if(comp instanceof Container container1){
                aplicarFonteEmTudo(container1, font);
            }
        }
        
        Font fonte = carregarFonte(36f);
        operacaoMat.setFont(fonte);
        jblContador.setFont(fonte);
        jContadorNivel.setFont(fonte);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaJogo().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt0;
    private javax.swing.JButton bt1;
    private javax.swing.JButton bt2;
    private javax.swing.JButton bt3;
    private javax.swing.JButton bt4;
    private javax.swing.JButton bt5;
    private javax.swing.JButton bt6;
    private javax.swing.JButton bt7;
    private javax.swing.JButton bt8;
    private javax.swing.JButton bt9;
    private javax.swing.JButton btMenos;
    private javax.swing.JButton btVirgular;
    private javax.swing.JButton enter;
    private javax.swing.JLabel jContadorNivel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jNivle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JButton jbApagar;
    private javax.swing.JLabel jblContador;
    private javax.swing.JLabel jlAcertos;
    private javax.swing.JLabel jlErros;
    private javax.swing.JTextField operacaoMat;
    private javax.swing.JButton voltar;
    // End of variables declaration//GEN-END:variables
}
