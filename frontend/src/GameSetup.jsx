import { useState } from 'react';
import { Link } from 'react-router-dom';

function GameSetup({ onStartGame }) {
  const [boardSize, setBoardSize] = useState(3);
  const [opponentType, setOpponentType] = useState('BOT_EASY');
  const [playerSymbol, setPlayerSymbol] = useState('X');

  const handleSubmit = (e) => {
    e.preventDefault();
    onStartGame({
      boardSize: Number(boardSize),
      opponentType,
      playerSymbol
    });
  };

  const renderPlayerChoice = () => {
    if (opponentType === 'HUMAN') {
      if (playerSymbol !== 'X') setPlayerSymbol('X');
      return null;
    }
    return (
      <div>
        <label>Choose your side: </label>
        <label>
          <input
            type="radio"
            value="X"
            checked={playerSymbol === 'X'}
            onChange={(e) => setPlayerSymbol(e.target.value)}
          /> X (Start First)
        </label>
        <label>
          <input
            type="radio"
            value="O"
            checked={playerSymbol === 'O'}
            onChange={(e) => setPlayerSymbol(e.target.value)}
          /> O (Start Second)
        </label>
      </div>
    );
  };

  return (
    <div className="setup-container">
      <div className="card" style={{width: '540px'}}>
        <h1 style={{ whiteSpace: 'nowrap', textAlign: 'center'}}>Tic-Tac-Toe (N x N)</h1>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Board Size (e.g., 3 for 3x3): </label>
            <input
              type="number"
              min="3"
              value={boardSize}
              onChange={(e) => setBoardSize(e.target.value)}
              required
            />
          </div>
          <div>
            <label>Opponent: </label>
            <select
              value={opponentType}
              onChange={(e) => setOpponentType(e.target.value)}
            >
              <option value="HUMAN">Human</option>
              <option value="BOT_EASY">Bot (Easy)</option>
              <option value="BOT_MEDIUM">Bot (Medium)</option>
              <option value="BOT_HARD">Bot (Hard)</option>
            </select>
          </div>
          {renderPlayerChoice()}
          <button type="submit">Start Game</button>
        </form>

        <div style={{ marginTop: '30px', textAlign: 'center' }}>
          <Link to="/results">
            View Result Board
          </Link>
        </div>
      </div>
    </div>
  );
}

export default GameSetup;