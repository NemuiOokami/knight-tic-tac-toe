package tournamentgui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import javax.swing.BorderFactory;

enum FieldState {
    PASSIVE, BKNIGHT, WKNIGHT, BKNIGHTA, WKNIGHTA, DESTINATION
}

/**
 * Egy mezőt reprezentál, tartalmazza a mező gombját, a státuszát, és a táblán
 * levő elhelyezkedését, x és y koordináták formájában.
 */
class Field {

    protected JButton button;
    protected FieldState state;
    protected int x;
    protected int y;
}

public class TournamentGUI {

    private final JFrame frame;
    private final JPanel tablePanel;
    private final JMenuBar menuBar;
    private final JMenu newMenu;
    private final JMenuItem field4x4Item;
    private final JMenuItem field6x6Item;
    private final JMenuItem field8x8Item;
    private final ImageIcon blackKnight;
    private final ImageIcon whiteKnight;
    private final ImageIcon icon;
    private Field[][] table;
    private int tableSize;

    public TournamentGUI() {
        frame = new JFrame("The Tournament");
        icon = new ImageIcon("resources/black.png");
        frame.setIconImage(icon.getImage());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tablePanel = new JPanel();
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        newMenu = new JMenu("New Game");
        field4x4Item = new JMenuItem("4x4");
        field6x6Item = new JMenuItem("6x6");
        field8x8Item = new JMenuItem("8x8");
        newMenu.add(field4x4Item);
        newMenu.add(field6x6Item);
        newMenu.add(field8x8Item);
        menuBar.add(newMenu);
        blackKnight = new ImageIcon("resources/black.png");
        whiteKnight = new ImageIcon("resources/white.png");
        field4x4Item.addActionListener((ActionEvent ae) -> createTable(4));
        field6x6Item.addActionListener((ActionEvent ae) -> createTable(6));
        field8x8Item.addActionListener((ActionEvent ae) -> createTable(8));
        frame.getContentPane().add(BorderLayout.CENTER, tablePanel);
        createTable(6);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
    }

    /**
     *
     * @param a Értéke a kiválasztott menüponttól függően 4,6 vagy 8. A pálya
     * méretét adja meg. Egy pálya mérete a*a.
     *
     * A kiválasztott menüponttól függő mérettel létrehozza a játékteret. A
     * konstruktor 6-al alapból meghívja, hogy megnyitáskor már egy táblát
     * lásson a felhasználó.
     */
    protected void createTable(int a) {
        tableSize = a;
        tablePanel.removeAll();
        tablePanel.revalidate();
        tablePanel.repaint();
        tablePanel.setLayout(new GridLayout(a, a));
        table = new Field[a][a];
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < a; ++j) {
                table[i][j] = new Field();
                table[i][j].button = new JButton();
                tablePanel.add(table[i][j].button);
                table[i][j].button.setEnabled(false);
                table[i][j].x = i;
                table[i][j].y = j;
                if (!(i == 0 && j == 0) && Math.abs(i - j) < a - 1 && !(i == a - 1 && j == a - 1)) {
                    table[i][j].button.addActionListener(new DestinationActionListener());
                    table[i][j].state = FieldState.PASSIVE;
                    table[i][j].button.setBackground(Color.GRAY);
                } else {
                    table[i][j].button.addActionListener(new PlayerActionListener());
                }
                table[i][j].button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                switch (a) {
                    case 4:
                        frame.setPreferredSize(new Dimension(400, 400));
                        frame.pack();
                        break;
                    case 6:
                        frame.setPreferredSize(new Dimension(600, 600));
                        frame.pack();
                        break;
                    case 8:
                        frame.setPreferredSize(new Dimension(800, 800));
                        frame.pack();
                        break;
                }
            }
        }
        table[0][0].button.setIcon(whiteKnight);
        table[a - 1][a - 1].button.setIcon(whiteKnight);
        table[0][a - 1].button.setIcon(blackKnight);
        table[a - 1][0].button.setIcon(blackKnight);
        table[0][0].button.setBackground(Color.WHITE);
        table[a - 1][a - 1].button.setBackground(Color.WHITE);
        table[0][a - 1].button.setBackground(Color.BLACK);
        table[a - 1][0].button.setBackground(Color.BLACK);
        table[0][0].state = FieldState.WKNIGHT;
        table[a - 1][a - 1].state = FieldState.WKNIGHT;
        table[0][a - 1].state = FieldState.BKNIGHT;
        table[a - 1][0].state = FieldState.BKNIGHT;
        table[0][0].button.setEnabled(true);
        table[a - 1][a - 1].button.setEnabled(true);
    }

    /**
     * Olyan gombok rendelkeznek vele, melynek mezején játékos áll. Ha rányomunk
     * egy ilyan gombra, kiválasztott státuszúvá teszi az adott játékost, és
     * megmutatja, hova léphet, ezeket a mezőket DESTINATION státuszúvá
     * változtatja. Attól függően kinek a köre van, az ellenfél ilyen gombjai le
     * vannak tiltva. Váltani is lehet a két figura között, ilyenkor úgy rendezi
     * el a táblát, hogy nyoma se maradjon annak, hogy nem a második figurát
     * választottuk ki először.
     */
    class PlayerActionListener implements ActionListener {

        @Override

        public void actionPerformed(ActionEvent ae) {
            Field f = new Field();
            JButton src = (JButton) ae.getSource();
            for (int i = 0; i < tableSize; ++i) {
                for (int j = 0; j < tableSize; ++j) {
                    if (table[i][j].button == src) {
                        f = table[i][j];
                    }
                    if (table[i][j].state == FieldState.DESTINATION) {
                        table[i][j].button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                        table[i][j].state = FieldState.PASSIVE;
                        table[i][j].button.setEnabled(false);
                    }
                    if (table[i][j].state == FieldState.BKNIGHTA) {
                        table[i][j].state = FieldState.BKNIGHT;
                        table[i][j].button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                    }
                    if (table[i][j].state == FieldState.WKNIGHTA) {
                        table[i][j].state = FieldState.WKNIGHT;
                        table[i][j].button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                    }
                }
            }
            if (f.state == FieldState.BKNIGHT) {

                f.state = FieldState.BKNIGHTA;
                f.button.setBorder(BorderFactory.createLineBorder(Color.RED, 4));
            }
            if (f.state == FieldState.WKNIGHT) {
                f.state = FieldState.WKNIGHTA;
                f.button.setBorder(BorderFactory.createLineBorder(Color.RED, 4));
            }
            int[][] posDes = {{f.x - 2, f.x - 1, f.x - 2, f.x - 1, f.x + 1, f.x + 2, f.x + 1, f.x + 2}, {f.y - 1, f.y - 2, f.y + 1, f.y + 2, f.y - 2, f.y - 1, f.y + 2, f.y + 1}};
            for (int i = 0; i < 8; ++i) {
                if (posDes[0][i] >= 0 && posDes[0][i] < tableSize && posDes[1][i] >= 0 && posDes[1][i] < tableSize) {
                    if (table[posDes[0][i]][posDes[1][i]].state != FieldState.BKNIGHT && table[posDes[0][i]][posDes[1][i]].state != FieldState.WKNIGHT) {
                        table[posDes[0][i]][posDes[1][i]].button.setEnabled(true);
                        table[posDes[0][i]][posDes[1][i]].button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
                        table[posDes[0][i]][posDes[1][i]].state = FieldState.DESTINATION;
                    }
                }
            }

        }
    }

    /**
     *
     * Minden gomb, aminek mezején nem játékos van, rendelkezik vele. Ha játékos
     * kerül oda, elveszíti, és kap egy PlayerActionListenert. Ha rákattintunk
     * egy DestinationActionListener-es gombra, akkor az éppen kiválasztott
     * státuszú mezőn szereplő játékos átkerül oda. Azon gombok, ahová a játékos
     * nem léphet, bár rendelkeznek ilyen ActionListenerrel, le vannak tilva.
     * Ezután frissíti a táblát.
     */
    class DestinationActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JButton src = (JButton) ae.getSource();
            Field f = new Field();
            Field pf = new Field();
            for (int i = 0; i < tableSize; ++i) {
                for (int j = 0; j < tableSize; ++j) {
                    if (table[i][j].button == src) {
                        f = table[i][j];
                    }
                    if (table[i][j].state == FieldState.BKNIGHTA || table[i][j].state == FieldState.WKNIGHTA) {
                        pf = table[i][j];
                        pf.button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                    }
                }
            }
            f.button.setBackground(pf.button.getBackground());
            f.button.setIcon(pf.button.getIcon());
            pf.button.setIcon(null);
            for (ActionListener al : f.button.getActionListeners()) {
                f.button.removeActionListener(al);
            }
            for (ActionListener al : pf.button.getActionListeners()) {
                pf.button.removeActionListener(al);
            }
            f.button.addActionListener(new PlayerActionListener());
            pf.button.addActionListener(new DestinationActionListener());
            if (pf.state == FieldState.BKNIGHTA) {
                pf.state = FieldState.PASSIVE;
                f.state = FieldState.BKNIGHT;
            } else {
                pf.state = FieldState.PASSIVE;
                f.state = FieldState.WKNIGHT;
            }
            f.button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            FieldState next;
            if (f.state == FieldState.WKNIGHT) {
                next = FieldState.BKNIGHT;
            } else {
                next = FieldState.WKNIGHT;
            }
            for (int i = 0; i < tableSize; ++i) {

                for (int j = 0; j < tableSize; ++j) {
                    if (table[i][j].state == next) {
                        table[i][j].button.setEnabled(true);
                    } else {
                        table[i][j].button.setEnabled(false);
                    }
                    if (table[i][j].state == FieldState.DESTINATION) {
                        table[i][j].state = FieldState.PASSIVE;
                        table[i][j].button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                    }
                }
            }
            winnerCheck();
        }

        /**
         * Minden lépés után végigfut a táblán, és ellenőrzi, hogy van-e még
         * szürke mező. Ha van, nem csinál semmit. Ha nincs, akkor
         * összeszámolja, hogy a fekete és fehér játkosnak hányszor jött ki
         * egymás melett 4 mezője, oly módon, hogyha egymás mellett 5 mező van,
         * akkor az első 4 lehúzásra kerül, így a másodiktól az ötödikig nem
         * számít +1 pontnak. Ezután létrehot egy új táblát, ami méretet a
         * jelenlegiével megegyezik.
         */
        protected void winnerCheck() {
            int[][] check = new int[tableSize][tableSize];
            boolean ended = true;
            for (int i = 0; i < tableSize; ++i) {
                for (int j = 0; j < tableSize; ++j) {
                    if (table[i][j].button.getBackground() == Color.GRAY) {
                        ended = false;
                    }
                }
            }
            if (ended) {
                int wp = 0;
                int bp = 0;
                for (int i = 0; i < tableSize; ++i) {
                    for (int j = 0; j < tableSize - 3; ++j) {
                        if (check[i][j] == 0 && table[i][j].button.getBackground() == table[i][j + 1].button.getBackground()) {
                            if (check[i][j + 1] == 0 && table[i][j + 1].button.getBackground() == table[i][j + 2].button.getBackground()) {
                                if (check[i][j + 2] == 0 && check[i][j + 3] == 0 && table[i][j + 2].button.getBackground() == table[i][j + 3].button.getBackground()) {
                                    if (table[i][j].button.getBackground() == Color.WHITE) {
                                        wp++;
                                    } else {
                                        bp++;
                                    }
                                    for (int k = j; k < j + 4; ++k) {
                                        check[i][k] = 1;
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < tableSize - 3; ++i) {
                    for (int j = 0; j < tableSize; ++j) {
                        if (check[i][j] == 0 && table[i][j].button.getBackground() == table[i + 1][j].button.getBackground()) {
                            if (check[i + 1][j] == 0 && table[i + 1][j].button.getBackground() == table[i + 2][j].button.getBackground()) {
                                if (check[i + 2][j] == 0 && check[i + 3][j] == 0 && table[i + 2][j].button.getBackground() == table[i + 3][j].button.getBackground()) {
                                    if (table[i][j].button.getBackground() == Color.WHITE) {
                                        wp++;
                                    } else {
                                        bp++;
                                    }
                                    for (int k = i; k < i + 4; ++k) {
                                        check[k][j] = 1;
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < tableSize - 3; ++i) {
                    for (int j = 0; j < tableSize - 3; ++j) {
                        if (check[i][j] == 0 && table[i][j].button.getBackground() == table[i + 1][j + 1].button.getBackground()) {
                            if (check[i + 1][j + 1] == 0 && table[i + 1][j + 1].button.getBackground() == table[i + 2][j + 2].button.getBackground()) {
                                if (check[i + 2][j + 2] == 0 && check[i + 3][j + 3] == 0 && table[i + 2][j + 2].button.getBackground() == table[i + 3][j + 3].button.getBackground()) {
                                    if (table[i][j].button.getBackground() == Color.WHITE) {
                                        wp++;
                                    } else {
                                        bp++;
                                    }
                                    for (int k = 0; k < 4; ++k) {
                                        check[i + k][j + k] = 1;
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < tableSize - 3; ++i) {
                    for (int j = tableSize - 1; j > 2; --j) {
                        if (check[i][j] == 0 && table[i][j].button.getBackground() == table[i + 1][j - 1].button.getBackground()) {
                            if (check[i + 1][j - 1] == 0 && table[i + 1][j - 1].button.getBackground() == table[i + 2][j - 2].button.getBackground()) {
                                if (check[i + 2][j - 2] == 0 && check[i + 3][j - 3] == 0 && table[i + 2][j - 2].button.getBackground() == table[i + 3][j - 3].button.getBackground()) {
                                    if (table[i][j].button.getBackground() == Color.WHITE) {
                                        wp++;
                                    } else {
                                        bp++;
                                    }
                                    for (int k = 0; k < 4; ++k) {
                                        check[i + k][j - k] = 1;
                                    }

                                }
                            }
                        }
                    }
                }
                if (wp < bp) {
                    JOptionPane.showMessageDialog(frame, "The black player won!", "Game over!", JOptionPane.PLAIN_MESSAGE);
                } else if (wp > bp) {
                    JOptionPane.showMessageDialog(frame, "The white player won!", "Game over!", JOptionPane.PLAIN_MESSAGE);
                } else if (wp == bp) {
                    JOptionPane.showMessageDialog(frame, "It's a draw!", "Game over!", JOptionPane.PLAIN_MESSAGE);
                }
                createTable(tableSize);
            }
        }
    }
}
