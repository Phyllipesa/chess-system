package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {
  private int turn;
  private Color currentPlayer;
  private Board board;
  private List<Piece> piecesOnTheBoard = new ArrayList<>();
  private List<Piece> capturedPieces = new ArrayList<>();

  public ChessMatch() {
    board = new Board(8, 8);
    turn = 1;
    currentPlayer = Color.WHITE;
    initialSetup();
  }

  public int getTurn() {
    return turn;
  }

  public Color getCurrentPlayer() {
    return currentPlayer;
  }

  public ChessPiece[][] getPieces() {
    ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        mat[i][j] = (ChessPiece) board.piece(i, j);
      }
    }

    return mat;
  }

  public ChessPiece performChessMoves(ChessPosition sourcePosition, ChessPosition targetPosition) {
    Position source = sourcePosition.toPosition();
    Position target = targetPosition.toPosition();
    validateSourcePosition(source);
    validateTargetPosition(source, target);

    Piece capturePiece = makeMove(source, target);
    nextTurn();
    return (ChessPiece) capturePiece;
  }

  //Converte a posição de xadrez para uma posição de matriz normal
  //Faz a validação depois que o usuario digita a posição em que a peça esta.
  //Retorna os movimentos possiveis da peça dessa posição.
  public boolean[][] possibleMoves(ChessPosition sourcePosition) {
    Position position = sourcePosition.toPosition();
    validateSourcePosition(position);
    return board.piece(position).possibleMoves();
  }

  private Piece makeMove(Position source, Position target) {
    //Retira a peça de origem do tabuleiro
    Piece p = board.removePiece(source);
    //Caso tenha uma peça na posição de destino a mesma é capturada
    Piece capturedPiece = board.removePiece(target);
    //Coloca na posição de destino a peça que estavana posição de origem.
    board.placePiece(p, target);

    if (capturedPiece != null) {
      piecesOnTheBoard.remove(capturedPiece);
      capturedPieces.add(capturedPiece);
    }
    return capturedPiece;
  }

  //Se para a posição de
  private void validateSourcePosition(Position position) {
    if (!board.thereIsAPiece(position)) {
      throw new ChessException("There is no piece on source position");
    }

    /*
        Verifica se a cor escolhida pelo jogador é diferente da cor da peça selecionada.
     */
    if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
      throw new ChessException("The chosen piece is not yours");

    }

    if (!board.piece(position).isThereAnyPossibleMoves()) {
      throw new ChessException("There is no moves for the chosen piece");
    }
  }

  private void validateTargetPosition(Position source, Position target) {
  /*Se para a peça de origem a posição de destino não é um movimento possivel.
    Ao construir a peça "piece(source)" o programa já sabe sua identidade
      pois fornecemos a ele a posição de origem(source), e nela há uma peça.
      exemplo:
            !board.King

    Agora que temos a peça, vamos acessar o metodo a SUPERCLASSE "possibleMove(target)"
      para verificarmos os movimentos possiveis.
      exemplo:
            !board.King.possibleMove(target)
    */
    if (!board.piece(source).possibleMove(target)) {
      throw new ChessException("There chosen piece can't move to target position");
    }
  }

  private void  nextTurn() {
    turn++;
    currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private void placeNewPiece(char column, int row, ChessPiece piece) {
    //Coloca uma peça no tabuleiro
    board.placePiece(piece, new ChessPosition(column, row).toPosition());
    //Coloca essa peça na lista "piecesOnTheBoard"
    piecesOnTheBoard.add(piece);
  }

  private void initialSetup() {
    placeNewPiece('c', 1, new Rook(board, Color.WHITE));
    placeNewPiece('c', 2, new Rook(board, Color.WHITE));
    placeNewPiece('d', 2, new Rook(board, Color.WHITE));
    placeNewPiece('e', 2, new Rook(board, Color.WHITE));
    placeNewPiece('e', 1, new Rook(board, Color.WHITE));
    placeNewPiece('d', 1, new King(board, Color.WHITE));

    placeNewPiece('c', 7, new Rook(board, Color.BLACK));
    placeNewPiece('c', 8, new Rook(board, Color.BLACK));
    placeNewPiece('d', 7, new Rook(board, Color.BLACK));
    placeNewPiece('e', 7, new Rook(board, Color.BLACK));
    placeNewPiece('e', 8, new Rook(board, Color.BLACK));
    placeNewPiece('d', 8, new King(board, Color.BLACK));
  }
}
