/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.awt.*;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


/**
 *
 * @author TSONXU
 */
public class Minesweeper extends JFrame implements MouseListener {

    /**
     *
     * khoi tao bien toan cuc
     */
    JButton[][] btn = new JButton[20][25];

    JMenuBar menuBar;
    JMenuItem menuNew;

    JPanel pne;

    JLabel flagg;
    JLabel tme;

    MoreJFrame mrJF = new MoreJFrame();
    Notification note;

    ImageIcon iBum = new ImageIcon(getClass().getResource("bum.png"));
    ImageIcon iFlag = new ImageIcon(getClass().getResource("flag.png"));
    ImageIcon iBoom = new ImageIcon(getClass().getResource("boom.png"));

    int numCol;
    int numRow;
    int numBoom;
    int btnClicked;
    int min, sec;
    int[] btnAround = new int[8];
    int[] checkUp = new int[4];
    int[] checkRi = new int[4];
    int[] checkDw = new int[4];

    long start;

    Vector matrix;
    Vector cons;
    Vector boom_index;
    Vector boom_next;
    Vector flag = new Vector();
    Vector question = new Vector();
    Vector oldBtn = new Vector();

    /**
     *
     * phuong thuc tao giao dien
     */
    public Minesweeper(String title, int col, int row, int boom) {
        super(title);

        /*
        khai bao bien
         */
        int sizeBtn = 30;

        /*
        tien xu ly
         */
        numCol = col;
        numRow = row;
        numBoom = boom;

        this.setLayout(new BorderLayout());


        /*
        tao menu
         */
        menuBar = new JMenuBar();
        this.add(menuBar, BorderLayout.NORTH);

        menuNew = new JMenuItem("New Game");
        menuNew.addMouseListener(this);
        menuBar.add(menuNew);

        /*
        tao khung tro choi
         */
        pne = new JPanel();
        pne.setLayout(new GridLayout(row, col));
        pne.setSize(new Dimension(sizeBtn * numCol, sizeBtn * numRow));
        this.add(pne, BorderLayout.CENTER);

        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                String txt_name = String.valueOf(j * 100 + i);
                btn[i][j] = new JButton();
                btn[i][j].setVisible(true);
                btn[i][j].setName(txt_name);
                btn[i][j].setFont(new Font("Arial", Font.BOLD, 13));
                btn[i][j].setForeground(Color.BLUE);
                btn[i][j].setBackground(Color.GRAY);
                btn[i][j].setSize(sizeBtn, sizeBtn);
                btn[i][j].addMouseListener(this);
                pne.add(btn[i][j]);
            }

        }

        /*
        footer
         */
        flagg = new JLabel("    Flag = " + String.valueOf(numBoom));
        tmeWatch();
        flagg.setFont(flagg.getFont().deriveFont(16.0f));
        this.add(flagg, BorderLayout.SOUTH);
        /*
        tao thong so co ban de chay chuong trinh
         */
        createConst();

        start = System.currentTimeMillis();
    }

    /**
     *
     * dong ho
     */
    public void tmeWatch() {
        try {
            long end = System.currentTimeMillis();
            long diff = end - start;
            sec = Integer.valueOf(String.valueOf(diff / 1000));
            min = sec / 60;
            sec = sec % 60;
            String s;
            s = String.format("  Flag = %d. Your time = %02d:%02d", numBoom - flag.size(), min, sec);
            flagg.setText(s);

        } catch (Exception e) {

        }
    }

    /**
     *
     * ham nhan suc kien mouse click
     */
    public void mouseExited(java.awt.event.MouseEvent ex) {

    }

    public void mouseEntered(java.awt.event.MouseEvent en) {
        tmeWatch();

    }

    public void mousePressed(java.awt.event.MouseEvent p) {
//        System.out.println(oldBtn.size());
//        System.out.println(matrix.size() - numBoom);
//        System.out.println("bat su kien menu");

        /*
        bat su kien menu
         */
        if (p.getSource() == menuNew) {
//            System.out.println("minesweeper");
            mrJF.setVisible(true);
            this.setVisible(false);
        } else {

//            System.out.println("bat su kien button");
            /*
                bat su kien button
             */
            String bt_info = p.toString();
            char[] final_ch = new char[4];

            final_ch[0] = bt_info.charAt(bt_info.length() - 4);
            final_ch[1] = bt_info.charAt(bt_info.length() - 3);
            final_ch[2] = bt_info.charAt(bt_info.length() - 2);
            final_ch[3] = bt_info.charAt(bt_info.length() - 1);

            for (int i = 0; i < 4; i++) {
                if (!cons.contains(final_ch[i])) {
                    final_ch[i] = '0';
                }
            }

//            System.out.println(final_ch);
            btnClicked = Integer.parseInt(String.copyValueOf(final_ch));

//            System.out.println("xu ly nut nhan");
            /*
            xu ly nut nhan
             */
            if (p.getButton() == 1) {
                if (boom_index.contains(btnClicked) && !flag.contains(btnClicked)) {
                    boomClicked();
                } else if (!boom_index.contains(btnClicked) && !flag.contains(btnClicked)) {
                    nextBoomClicked();
                }
            } else {
                if (!oldBtn.contains(btnClicked)) {
                    Vector v = new Vector();
                    v.addElement(btnClicked);
                    if (!question.contains(btnClicked)) {
                        if (!flag.contains(btnClicked) && flag.size() != numBoom) {
                            btn[btnClicked % 100][btnClicked / 100].setText("");
                            btn[btnClicked % 100][btnClicked / 100].setIcon(iFlag);
                            flag.add(btnClicked);
                            tmeWatch();
                        } else {
                            btn[btnClicked % 100][btnClicked / 100].setIcon(null);
                            btn[btnClicked % 100][btnClicked / 100].setText("?");
                            flag.removeAll(v);
                            question.add(v);
                            tmeWatch();
                        }
                    } else {
                        question.removeAll(v);
                        btn[btnClicked % 100][btnClicked / 100].setText("");
                        tmeWatch();
                    }
                }
            }

            /*
            xu ly khi thang
             */
            if (oldBtn.size() == (numRow * numCol - numBoom)) {
//                System.out.println(oldBtn.size());
                for (int i = 0; i < numRow; i++) {
                    for (int j = 0; j < numCol; j++) {
                        if (boom_index.contains(j * 100 + i)) {
                            btn[i][j].setText("");
                            btn[i][j].setIcon(iBoom);

                        }
                    }
                }
                tmeWatch();
                String s;
                s = String.format("<html><p class=\"alignCenter\">You Win!<br>Your time = %02d:%02d</p></html>", min, sec);
                note = new Notification(this, true, s, numRow, numCol, numBoom);
                note.setVisible(true);
                this.setVisible(false);
            }
        }

    }

    public void mouseClicked(java.awt.event.MouseEvent c) {

    }

    public void mouseReleased(java.awt.event.MouseEvent r) {

    }

    /**
     *
     * xu ly khi click trung boom
     */
    private void boomClicked() {
//        System.out.println("xu ly khi click trung boom");
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                if (boom_index.contains(j * 100 + i)) {
                    if (flag.contains(j * 100 + i)) {
                        btn[i][j].setText("");
                        btn[i][j].setIcon(iBoom);
                    } else {
                        btn[i][j].setText("");
                        btn[i][j].setIcon(iBum);
                    }

                }
            }
        }
        String s;
        s = String.format("<html><p class=\"alignCenter\">You Close!<br>Your time = %02d:%02d</p></html>", min, sec);
        note = new Notification(this, true, s, numRow, numCol, numBoom);
        note.setVisible(true);
        this.setVisible(false);
    }

    /**
     *
     * xu li khi click phai o canh boom
     */
    private void nextBoomClicked() {
//        System.out.println("xu li khi click phai o canh boom");
        int numBoomAround;
        numBoomAround = checkAround(btnClicked);
        if (numBoomAround != 0) {
            btn[btnClicked % 100][btnClicked / 100].setText(String.valueOf(numBoomAround));
            if (!oldBtn.contains(btnClicked)) {
                oldBtn.add(btnClicked);
            }
        } else {
            btn[btnClicked % 100][btnClicked / 100].setBackground(Color.WHITE);
            checkNon();
        }
    }

    /**
     *
     * thuat toan vet dau loang
     */
    private void checkNon() {
        Vector checked = new Vector();
        Vector check = new Vector();
        int index = 0;

        checkAround(btnClicked);
        for (int i = 0; i < 8; i++) {
            if (!oldBtn.contains(btnAround[i]) && matrix.contains(btnAround[i])) {
                check.add(btnAround[i]);
            }
        }
//        System.out.println(check);
//        System.out.println(check.size());
        for (int i = 0; i < check.size(); i++) {
//            System.out.println(check.size());
            if (!oldBtn.contains(check.elementAt(i)) && matrix.contains(check.elementAt(i)) && !flag.contains(check.elementAt(i))) {
                index = Integer.parseInt(String.valueOf(check.elementAt(i)));
                if (!boom_next.contains(check.elementAt(i))) {
                    btn[index % 100][index / 100].setBackground(Color.WHITE);
                    btn[index % 100][index / 100].setText("");
                    oldBtn.add(check.elementAt(i));
                    checkAround(index);
                    for (int j = 0; j < 8; j++) {
                        if (!oldBtn.contains(btnAround[j]) && matrix.contains(index)) {
                            check.add(btnAround[j]);
                        }
                    }
//                    System.out.println(i);
//                    System.out.println(check);
                } else {
                    btn[index % 100][index / 100].setText(String.valueOf(countBoom(index)));
                    oldBtn.add(check.elementAt(i));
                }
            }
        }

    }

    /**
     *
     * dem boom khong lam thay doi gia tri
     */
    private int countBoom(int clicked) {
        tmeWatch();
        int numBoomAround;
        int[] btnA = new int[8];
        /*
        quet xung quanh
         */
        btnA[1] = clicked - 1;
        btnA[5] = btnA[1] - 100;
        btnA[6] = btnA[1] + 100;

        btnA[3] = clicked + 1;
        btnA[4] = btnA[3] - 100;
        btnA[7] = btnA[3] + 100;

        btnA[0] = clicked - 100;
        btnA[2] = clicked + 100;

        for (int i = 0; i < 8; i++) {
            if (!matrix.contains(btnA[i])) {
                btnA[i] = -102;
            }
        }

//        System.out.println("dem boom");
        /*
        dem boom
         */
        numBoomAround = 0;
        for (int i = 0; i < 8; i++) {
            if (boom_index.contains(btnA[i])) {
                numBoomAround++;
            }

        }

        return numBoomAround;
    }

    /**
     *
     * ham quet xung quanh va dem boom
     */
    private int checkAround(int clicked) {
        tmeWatch();
//        System.out.println("ham quet xung quanh");
        int numBoomAround;
        /*
        quet xung quanh
         */
        btnAround[1] = clicked - 1;
        btnAround[5] = btnAround[1] - 100;
        btnAround[6] = btnAround[1] + 100;

        btnAround[3] = clicked + 1;
        btnAround[4] = btnAround[3] - 100;
        btnAround[7] = btnAround[3] + 100;

        btnAround[0] = clicked - 100;
        btnAround[2] = clicked + 100;

        for (int i = 0; i < 8; i++) {
            if (!matrix.contains(btnAround[i])) {
                btnAround[i] = -102;
            }
        }

//        System.out.println("tao mang quet");
        /*
        tao mang quet
         */
        checkUp[0] = btnAround[3];
        checkUp[1] = btnAround[0];
        checkUp[2] = btnAround[1];
        checkUp[3] = btnAround[2];

        checkRi[0] = btnAround[2];
        checkRi[1] = btnAround[3];
        checkRi[2] = btnAround[0];
        checkRi[3] = btnAround[1];

        checkDw[0] = btnAround[1];
        checkDw[1] = btnAround[2];
        checkDw[2] = btnAround[3];
        checkDw[3] = btnAround[0];

//        System.out.println("dem boom");
        /*
        dem boom
         */
        numBoomAround = 0;
        for (int i = 0; i < 8; i++) {
            if (boom_index.contains(btnAround[i])) {
                numBoomAround++;
            }
//            System.out.println(btnAround[i]);
        }

        return numBoomAround;
    }

    /**
     *
     * ham tao cac hang so can thiet de xu ly chuong trinh
     */
    private void createConst() {
//        System.out.println("ham tao cac hang so can thiet de xu ly chuong trinh");
        matrix = new Vector();
        cons = new Vector();
        boom_index = new Vector();
        boom_next = new Vector();

        Random rd = new Random();

        int index;

//        System.out.println("tao vecto ma tran phim");
        /*
        tao vecto ma tran phim
         */
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                matrix.add(j * 100 + i);
            }
        }

        /*
        tao vecto boom
         */
        for (int i = 0; i < numBoom; i++) {
            index = rd.nextInt(numCol * numRow - 1);
            if (boom_index.contains(matrix.elementAt(index))) {
                i--;
            } else {
                boom_index.add(matrix.elementAt(index));
                checkAround(Integer.valueOf(matrix.elementAt(index).toString()));
                for (int j = 0; j < 8; j++) {
                    if (!boom_next.contains(btnAround[j])) {
                        boom_next.add(btnAround[j]);
                    }
                }
            }

        }

        cons.add('0');
        cons.add('1');
        cons.add('2');
        cons.add('3');
        cons.add('4');
        cons.add('5');
        cons.add('6');
        cons.add('7');
        cons.add('8');
        cons.add('9');
//        System.out.println(cons);

    }

}
