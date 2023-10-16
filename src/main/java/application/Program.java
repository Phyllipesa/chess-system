package application;

import chess.ChessMatch;
//ctrl + alt  o - Otimiza imports invalidos.

public class Program {
  public static void main(String[] args) {

    ChessMatch chessMatch = new ChessMatch();
    UI.printBoard(chessMatch.getPieces());
  }
}
