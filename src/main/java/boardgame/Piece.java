package boardgame;

public abstract  class Piece {
  protected Position position;
  private Board board;

  public Piece(Board board) {
    this.board = board;
    position = null;
  }

  protected Board getBoard() {
    return board;
  }

  public abstract boolean[][] possibleMoves();

  /*
    O método "possibleMove" da SUPERCLASSE(Piece) recebe a posição de uma peça identificada
      e passa sua localicação para o método "possibleMoves".
    Sabendo que o método "possibleMoves" é abstrato o programa chama a implementação do método
      na subclasse(King) onde está estabelecida a lógica de movimentação da peça.
   */
  public boolean possibleMove(Position position) {
    return possibleMoves()[position.getRow()][position.getColumn()];
  }

  public boolean isThereAnyPossibleMoves() {
    boolean[][] mat = possibleMoves();

    for (int i = 0; i < mat.length; i++) {
      for (int j = 0; j < mat.length; j++) {
        if (mat[i][j]) {
          return true;
        }
      }
    }
    return false;
  }
}
