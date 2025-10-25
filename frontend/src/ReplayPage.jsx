import { useState, useEffect } from 'react';
import apiClient from './api/api';
import { useParams, Link } from 'react-router-dom';
import Cell from './Cell';

function ReplayPage() {
  const { sessionId } = useParams();

  const [replayData, setReplayData] = useState(null);

  const [currentMoveIndex, setCurrentMoveIndex] = useState(-1);

  const [boardState, setBoardState] = useState([]);

  const [boardSize, setBoardSize] = useState(3);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    apiClient.get(`/api/replays/${sessionId}`)
      .then(response => {
        setReplayData(response.data);
      })
      .catch(err => setError('Failed to load replay.'))
      .finally(() => setIsLoading(false));
  }, [sessionId]);

  useEffect(() => {
    if (!replayData) return;

    const size = replayData.boardSize;

    setBoardSize(size);

    const newBoard = Array(size * size).fill('');

    for (let i = 0; i <= currentMoveIndex; i++) {
      const move = replayData.moves[i];
      const index = move.rowIndex * size + move.colIndex;
      newBoard[index] = move.player;
    }
    setBoardState(newBoard);

  }, [replayData, currentMoveIndex]);

  const boardStyle = {
    display: 'grid',
    gridTemplateColumns: `repeat(${boardSize}, 100px)`,
    gridTemplateRows: `repeat(${boardSize}, 100px)`,
    width: `${boardSize * 100}px`,
    margin: '20px auto'
  };

  if (isLoading) return <div>Loading Replay...</div>;
  if (error) return <div>{error}</div>;
  if (!replayData) return <div>No replay data.</div>;

  const handleNext = () => {
    if (currentMoveIndex < replayData.moves.length - 1) {
      setCurrentMoveIndex(currentMoveIndex + 1);
    }
  };

  const handlePrev = () => {
    if (currentMoveIndex > -1) {
      setCurrentMoveIndex(currentMoveIndex - 1);
    }
  };

  return (
    <div className="card">
      <h1>Replay</h1>
      <div className="nav-links">
        <Link to="/" role="button" className="secondary">Home</Link>
        <Link to="/results" role="button" className="secondary">Back to Results</Link>
      </div>
      <hr style={{ width: '100%' }} />

      <div style={boardStyle}>
        {boardState.map((value, index) => (
          <Cell key={index} value={value} onClick={() => { }} />
        ))}
      </div>

      <div className="controls-container">

        <button onClick={() => setCurrentMoveIndex(-1)} disabled={currentMoveIndex === -1}>
          Reset (Start)
        </button>

        <div className="separator"></div>

        <div className="prev-next-group">
          <button onClick={handlePrev} disabled={currentMoveIndex <= -1}>
            Previous
          </button>
          <button onClick={handleNext} disabled={currentMoveIndex >= replayData.moves.length - 1}>
            Next
          </button>
        </div>
      </div>

      <h3>
        Move: {currentMoveIndex + 1} / {replayData.moves.length}
      </h3>
      <p>(You played as: {replayData.humanPlayerSymbol})</p>
    </div>
  );
}

export default ReplayPage;