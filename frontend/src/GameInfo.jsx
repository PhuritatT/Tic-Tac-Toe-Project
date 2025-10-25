function GameInfo({ session, onSurrender, onNewGame }) {
  const { gameStatus, nextPlayer } = session;

  const getStatusMessage = () => {
    if (gameStatus === 'IN_PROGRESS') {
      return `Turn: ${nextPlayer}`;
    }
    if (gameStatus === 'X_WINS') {
      return 'Player X Wins!';
    }
    if (gameStatus === 'O_WINS') {
      return 'Player O Wins!';
    }
    if (gameStatus === 'DRAW') {
      return "It's a Draw!";
    }
    return gameStatus;
  };

  return (
    <div className="game-info">
      <h2>{getStatusMessage()}</h2>
      
      {gameStatus === 'IN_PROGRESS' && (
        <button onClick={onSurrender}>Surrender</button>
      )}

      {gameStatus !== 'IN_PROGRESS' && (
        <button onClick={onNewGame}>Play New Game</button>
      )}

    </div>
  );
}

export default GameInfo;