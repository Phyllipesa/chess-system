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
  private boolean check;
  private boolean checkMate;
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

  public boolean getCheck() {
    return check;
  }

  public boolean getCheckMate() {
    return checkMate;
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

    if (testCheck(currentPlayer)) {
      undoMove(source, target, capturePiece);
      throw new ChessException("You can't put yourself in check!");
    }

    check = testCheck(opponent(currentPlayer));

    if(testCheckMate(opponent(currentPlayer))) {
      checkMate = true;
    } else {
      nextTurn();
    }

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

  /*
    makeMove
      Retira a peça de origem do tabuleiro
      Caso tenha uma peça na posição de destino a mesma é capturada
      Coloca na posição de destino a peça que estavana posição de origem.
   */
  private Piece makeMove(Position source, Position target) {
    Piece p = board.removePiece(source);
    Piece capturedPiece = board.removePiece(target);
    board.placePiece(p, target);

    if (capturedPiece != null) {
      piecesOnTheBoard.remove(capturedPiece);
      capturedPieces.add(capturedPiece);
    }
    return capturedPiece;
  }

  /*
    undoMove
      Retira a peça de destino.
      Devolte a peça para a posição de origem.
      Se no movimento houve a captura de uma peça, retorne a peça para o tabuleiro na posição de destino.
 */
  private void undoMove(Position source, Position target, Piece capturedPiece) {
    Piece p = board.removePiece(target);
    board.placePiece(p, source);

    if (capturedPiece != null) {
      board.placePiece(capturedPiece, target);
      capturedPieces.remove(capturedPiece);
      piecesOnTheBoard.add(capturedPiece);
    }
  }

  private void validateSourcePosition(Position position) {
    /*
        Verifica se não existe uma peçana posição escolhida.
        Verifica se a cor escolhida pelo jogador é diferente da cor da peça selecionada.
        Verifica se não existe movimentos para a peça escolhida.
     */
    if (!board.thereIsAPiece(position)) {
      throw new ChessException("There is no piece on source position");
    }

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

  //Retorna a cor do oponente
  private Color opponent(Color color) {
    return  (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private ChessPiece King(Color color) {
    List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).toList();

    for (Piece p : list) {
      if (p instanceof King) {
        return (ChessPiece)p;
      }
    }
    throw new IllegalStateException("There is no " + color + " King on the board");
  }

    /*
      testCheck
        Pega a posição do King no formato de matriz.
        Faz uma lista das peças que o oponente tem na mesa
          Cada iteração do FOR vai pegar os movimentos possiveis da peça P.
          Verifica se em algum dos movimentos possiveis está o rei inimigo e retorna TRUE
          confirmando que o King esta em CHECK em relação a P.
    */
  private boolean testCheck(Color color) {
    Position KingPosition = King(color).getChessPosition().toPosition();
    List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).toList();
    for (Piece p : opponentPieces) {
      boolean[][] mat = p.possibleMoves();
      if (mat[KingPosition.getRow()][KingPosition.getColumn()]) {
        return true;
      }
    }
    return false;
  }

  /*
      testCheckMate
        Verifica se a cor atual não esta em check.
   */
  private boolean testCheckMate(Color color) {
    if (!testCheck(color)) {
      return false;
    }

    List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).toList();
    for (Piece p : list) {
      boolean[][] mat = p.possibleMoves();
      for (int i = 0; i < board.getRows(); i++) {
        for (int j = 0; j < board.getColumns(); j++) {
          if (mat[i][j]) {
            Position source = ((ChessPiece)p).getChessPosition().toPosition();
            Position target = new Position(i, j);
            Piece capturedPiece = makeMove(source, target);
            boolean testCheck = testCheck(color);
            undoMove(source, target, capturedPiece);
            if (!testCheck) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  private void placeNewPiece(char column, int row, ChessPiece piece) {
    //Coloca uma peça no tabuleiro
    board.placePiece(piece, new ChessPosition(column, row).toPosition());
    //Coloca essa peça na lista "piecesOnTheBoard"
    piecesOnTheBoard.add(piece);
  }

  private void initialSetup() {
    placeNewPiece('h', 7, new Rook(board, Color.WHITE));
    placeNewPiece('d', 1, new Rook(board, Color.WHITE));
    placeNewPiece('e', 1, new King(board, Color.WHITE));

    placeNewPiece('b', 8, new Rook(board, Color.BLACK));
    placeNewPiece('a', 8, new King(board, Color.BLACK));
  }
}
