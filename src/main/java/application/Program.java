package application;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
//ctrl + alt  o - Otimiza imports invalidos.

public class Program {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    ChessMatch chessMatch = new ChessMatch();
    List<ChessPiece> captured = new ArrayList<>();

    while (!chessMatch.getCheckMate()) {
      try {
        UI.clearScreen();
        UI.printMatch(chessMatch, captured);
        System.out.println();
        System.out.print("Source: ");
        ChessPosition source = UI.readChessPosition(sc);

        //Pega a posição da peça que o usuario escolheu e printa quais os movimentos possiveis desta peça a partir dessa posição.
        boolean [][] possibleMoves = chessMatch.possibleMoves(source);
        UI.clearScreen();
        UI.printBoard(chessMatch.getPieces(), possibleMoves);

        System.out.println();
        System.out.print("Target: ");
        ChessPosition target = UI.readChessPosition(sc);

        ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

        //Verifica se alguma peça foi capturada
        //  Se sim, essa peça é adicionada a array captured.
        if (capturedPiece != null) {
          captured.add(capturedPiece);
        }
      }
      catch (ChessException | InputMismatchException e) {
        System.out.println(e.getMessage());
        sc.nextLine();
      }
    }
    UI.clearScreen();
    UI.printMatch(chessMatch, captured);
  }
}
