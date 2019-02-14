package GUI;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;
import GameLogic.Game.GameType;
import GameLogic.Piece;
import GameLogic.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * The GipfWindow uses a GipfBoardComponent to show the board. More information about the game can be added to the window
 * <p/>
 * Created by frans on 18-9-2015.
 */
public class GipfWindow extends JFrame {
    JTextArea gameLogTextArea;
    private final JTextField newPieceCoordinateTextField;
    private final JComboBox<Piece> pieceTypeComboBox;
    private final JLabel piecesLeftLabel;
    private final JLabel currentPlayerLabel;
    private final JLabel gameTypeLabel;
    private GameStateUpdater gameStateUpdater;
    private GipfBoardComponent gipfBoardComponent;
    ActionListener openDialog = e -> {
        JFileChooser fileChooser = new JFileChooser();
        int rval = fileChooser.showOpenDialog(this);

        if (rval == JFileChooser.APPROVE_OPTION) {
            try {
                FileInputStream fileIn = new FileInputStream(fileChooser.getSelectedFile().getAbsolutePath());
                ObjectInputStream in = null;
                in = new ObjectInputStream(fileIn);

                Game game = (Game) in.readObject();
                in.close();
                fileIn.close();

                gipfBoardComponent.setGame(game);
                gameLogTextArea.setText("");
                gipfBoardComponent.game.newGameLogger();
                gameStateUpdater.setGame(game);
                System.out.println("Game is opened from " + fileChooser.getSelectedFile().getAbsolutePath());
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    };
    ActionListener saveDialog = e -> {
        JFileChooser fileChooser = new JFileChooser();
        int rval = fileChooser.showSaveDialog(this);

        if (rval == JFileChooser.APPROVE_OPTION) {
            try {
                FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath());
                ObjectOutputStream out = null;
                out = new ObjectOutputStream(fileOut);

                out.writeObject(gipfBoardComponent.game);
                out.close();
                fileOut.close();
                System.out.println("Game is saved in " + fileChooser.getSelectedFile().getAbsolutePath());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    };

    public GipfWindow() throws HeadlessException {
        super();

        // Initialize the fields
        final JPanel contentPane = new JPanel();
        newPieceCoordinateTextField = new JTextField();
        JButton newPieceCoordinateEnterButton = new JButton("Enter");
        JButton previousStateButton = new JButton("Undo move");
        JLabel theGipfGameLabel = new JLabel("The GIPF game");
        Game game = new BasicGame();
        gipfBoardComponent = new GipfBoardComponent(game);
        gameLogTextArea = new DebugTextArea();
        pieceTypeComboBox = new JComboBox<>(Piece.values());
        piecesLeftLabel = new JLabel(" ");
        currentPlayerLabel = new JLabel(" ");
        gameTypeLabel = new JLabel(" ");
        gameStateUpdater = new GameStateUpdater(this, game);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveAsMenuItem = new JMenuItem("Save as...");
        JMenuItem openMenuItem = new JMenuItem("Open...");
        JMenuItem closeMenuItem = new JMenuItem("Close");
        JMenu newGameMenu = new JMenu("New");
        JMenuItem newBasicGameMenuItem = new JMenuItem("Basic game");
        JMenuItem newStandardGameMenuItem = new JMenuItem("Standard game");
        JMenuItem newTournamentGameMenuItem = new JMenuItem("Tournament game");

        menuBar.add(fileMenu);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(closeMenuItem);
        menuBar.add(newGameMenu);
        newGameMenu.add(newBasicGameMenuItem);
        newGameMenu.add(newStandardGameMenuItem);
        newGameMenu.add(newTournamentGameMenuItem);

        // Set the properties of the elements
        gameLogTextArea.setRows(10);
        currentPlayerLabel.setFont(UIval.get().largeLabelFont);
        gameTypeLabel.setFont(UIval.get().largeLabelFont);
        piecesLeftLabel.setFont(UIval.get().largeLabelFont);
        theGipfGameLabel.setFont(UIval.get().largeLabelFont);
        previousStateButton.setFont(UIval.get().buttonFont);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GIPF");

        getContentPane().add(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));    // Put everything in a column

        setJMenuBar(menuBar);
        contentPane.add(theGipfGameLabel);
        contentPane.add(gameTypeLabel);
        contentPane.add(currentPlayerLabel);
        contentPane.add(piecesLeftLabel);
        contentPane.add(gipfBoardComponent);
        contentPane.add(previousStateButton);

        // Add listeners
        newPieceCoordinateTextField.addActionListener(e -> listenerAddNewPiece());
        newPieceCoordinateEnterButton.addActionListener(e -> listenerAddNewPiece());
        previousStateButton.addActionListener(e -> {
            returnToPreviousState();
            gipfBoardComponent.clearSelectedPositions();
        });

        openMenuItem.addActionListener(openDialog);
        saveAsMenuItem.addActionListener(saveDialog);
        closeMenuItem.addActionListener(e -> System.exit(0));
        newBasicGameMenuItem.addActionListener(e -> newGame(GameType.basic));
        newStandardGameMenuItem.addActionListener(e -> newGame(GameType.standard));
        newTournamentGameMenuItem.addActionListener(e -> newGame(GameType.tournament));


        contentPane.add(new JScrollPane(gameLogTextArea));
        previousStateButton.setFocusable(false);                // To avoid the flashing undo button

        pack();
        setVisible(true);
        new Thread(gameStateUpdater).run();
    }

    public static void main(String argv[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        new GipfWindow();
    }

    private void listenerAddNewPiece() {
        String newCoordinateText = newPieceCoordinateTextField.getText();
        newPieceCoordinateTextField.setText("");
        newPieceCoordinateTextField.requestFocus();

        try {
            char colName = newCoordinateText.charAt(0);
            int rowNumber = Character.digit(newCoordinateText.charAt(1), 10);   // Convert the second character to a digit in base 10
            Position newPiecePosition = new Position(colName, rowNumber);

            if (gipfBoardComponent.game.isPositionOnBigBoard(newPiecePosition)) {
                Piece pieceType = (Piece) pieceTypeComboBox.getModel().getSelectedItem();

                gameLogTextArea.append("Placing new " + pieceType + " at " + newPiecePosition.getName());

                gipfBoardComponent.game.getGipfBoardState().getPieceMap().put(newPiecePosition, pieceType);
                gipfBoardComponent.repaint();
            } else {
                gameLogTextArea.append("Position " + newPiecePosition.getName() + " is invalid");
            }
        } catch (Exception e) {
            gameLogTextArea.append("Can't parse '" + newCoordinateText + "'");
        }
    }

    private void returnToPreviousState() {
        gipfBoardComponent.game.returnToPreviousBoard();
    }

    public void setPiecesLeftLabel(String message) {
        piecesLeftLabel.setText(message);
    }

    public void setCurrentPlayerLabel(String message) {
        currentPlayerLabel.setText(message);
    }

    public void setGameTypeLabel(String message) {
        gameTypeLabel.setText(message);
    }

    private void newGame(GameType gameType) {
        gipfBoardComponent.newGame(gameType);
        gameLogTextArea.setText("");
        gameStateUpdater.setGame(gipfBoardComponent.game);
    }
}
