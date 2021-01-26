/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;
import model.Student;

/**
 *
 * @author Jorge
 */
public class Jolasa extends JFrame {
    
    Timer timer;
    int frameCounter = 0;
    int xsize = 1400; // Frame zabalera
    int ysize = 700; // Frame altuera
    private final int MARRAZKI_ZABALERA = 1100; // Non pintatuko da
    private final int MARRAZKI_ALTUERA = 500; // Non pintatuko da
    private final int ESKERREKO_PADDINA = (xsize - MARRAZKI_ZABALERA) / 2;
    private final int GOIKO_PADDINA = (ysize - MARRAZKI_ALTUERA) / 2;
    private final int PERTSONEN_SEPARADOREA = 100;
    private final int NUM_PERTSONAK = 5;
    
    // Demo datuak
    private double korrikalariakAbiadura[] = {0.25, 0.7, 0.6, 0.5, 0.4};
    // Kotxearen koloreak
    private final Color COLORS[] = {Color.RED, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.ORANGE};
    
    private String izenak[] = {"Jorge", "Eneko", "Mikel", "Alvaro", "Tamara"};

    //           4
    //              1
    //             2
    //            3
    // INDEX:    0  1  2  3
    // POSIZIOA: 2, 3, 4, 1
    private ArrayList<Integer> indexIrabazleak = new ArrayList();
    
    public static void main(String[] args) {
        try {
            JFrame jolasa = new Jolasa();
            jolasa.setVisible(true);
            
            while (true) {
                Thread.sleep(100);
                jolasa.repaint();
            }
        } catch (Exception e) {
            System.err.println("Errore bat gertatu da.\n" + e);
        }
    }
    
    public Jolasa() {
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(xsize, ysize);
        this.setTitle("Lasterketa Jolasa");
    }
    
    public Jolasa(ArrayList<Student> estadisticas) {
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(xsize, ysize);
        this.setTitle("Lasterketa Jolasa");
        
        Random rand = new Random();

        // Selecciona this.NUM_PERTSONAK elementos aleatoriamente del arraylist
        for (int i = 0; i < this.NUM_PERTSONAK; i++) {
            int randomIndex = rand.nextInt(estadisticas.size());
            Student student = estadisticas.get(randomIndex);
            estadisticas.remove(randomIndex);
            
            this.korrikalariakAbiadura[i] = student.getAvgScore() / 100.0;
            this.izenak[i] = student.getName();
        }
    }

    @Override
    public void paint(Graphics g) {
        int ypos = 1;
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fontMetrics = g2d.getFontMetrics();

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, xsize, ysize);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", 20, 20));
        
        String titulo = "LASTERKETA JOLASA";
        g2d.drawString(titulo, xsize / 2 - fontMetrics.stringWidth(titulo), 75);

        g2d.setColor(Color.BLACK);
    
        // Korrikalarien izenak jartzeko
        for (int i = 0; i < izenak.length; i++) {
            g2d.setFont(new Font("Arial", 15, 15));
            g2d.drawString(izenak[i], 120 - fontMetrics.stringWidth(izenak[i]), 55 + ((i+1) * PERTSONEN_SEPARADOREA));
        }
        
        // HELBURURAKO
        g2d.setFont(new Font("Arial", 30, 30));
        
        barrakMarraztu(g2d, NUM_PERTSONAK, ESKERREKO_PADDINA, GOIKO_PADDINA, MARRAZKI_ZABALERA, PERTSONEN_SEPARADOREA);
        helburuaMarraztu(g2d, xsize - 200, GOIKO_PADDINA, GOIKO_PADDINA + (NUM_PERTSONAK * PERTSONEN_SEPARADOREA));
        
        g2d.setColor(Color.GREEN);
        idatziBertikalean(g2d, xsize - 135, 190, 50, "HELBURUA");
        
        marraztuKotxeak(g2d, NUM_PERTSONAK, 20, ESKERREKO_PADDINA, GOIKO_PADDINA + 25, PERTSONEN_SEPARADOREA, 50, xsize - 200);
        
        // Importante, cada vez que repinto, aumento el numero de fotograma
        this.frameCounter++;
        
    }
    
    private void marraztuKotxeak(Graphics2D g2d, int num_pertsonak, double speed, int x, int y, int separadorea, int tamania, int helburua) {
        for (int i = 0; i < num_pertsonak; i++) {
            double avgScore = this.korrikalariakAbiadura[i];
            double korrikalariakSpeed = avgScore * speed;
            int final_x = (int)(x + korrikalariakSpeed * this.frameCounter);
            
            if (final_x > helburua) {
                final_x = helburua;
                
                if (!indexIrabazleak.contains(i)) {
                    indexIrabazleak.add(i);
                }
            }
            
            g2d.setColor(COLORS[i]);
            g2d.fillRect(final_x, y + (i * separadorea), tamania, tamania);
        }
        
        // Print winners
        for (int i = 0; i < indexIrabazleak.size(); i++){
            int irabazleIndex = indexIrabazleak.get(i);
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(i+1), helburua + 17, y + (irabazleIndex * separadorea) + 35);
        }
    }
    
    private void barrakMarraztu(Graphics2D g2d, int numBarrak, int x, int y, int final_x, int separadorea){
        // Pinto las barras -        
        for (int i = 0; i <= numBarrak; i++){
            g2d.drawLine(x, y + (i * separadorea), x + final_x, y + (i * separadorea));
        }
    }
    
    private void helburuaMarraztu(Graphics2D g2d, int x, int y, int final_y){
        int sizeLaukia = (final_y - y)/10;
        Color currentColor = Color.BLACK;

        for (int i = 0; i< 10; i++) {
            for (int j = 0; j < 3; j++) {
                g2d.setColor(currentColor);
                g2d.fillRect(x + (j * sizeLaukia) , y + (i * sizeLaukia), sizeLaukia, sizeLaukia);
                
                if (currentColor == Color.BLACK) {
                    currentColor = Color.WHITE;
                } else {
                    currentColor = Color.BLACK;
                }
            }
        }
    }
    
    private void idatziBertikalean(Graphics2D g2d, int x, int y, int separadorea, String testua){
        for (int i = 0; i< testua.length(); i++){
            char karaktereak = testua.charAt(i);
            g2d.drawString(String.valueOf(karaktereak), x, y + i * separadorea);
        }
        
    }
}
