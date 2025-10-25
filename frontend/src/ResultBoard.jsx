import { useState, useEffect } from 'react';
import apiClient from './api/api';
import { Link } from 'react-router-dom';

function ResultBoard() {
  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    apiClient.get('/api/results')
      .then(response => {
        setResults(response.data);
      })
      .catch(error => {
        console.error('Error fetching results:', error);
        setError('Failed to load results.');
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  if (isLoading) return <div>Loading Results...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className='card'>
      <div className="result-board-container">
        <h1>Game Results</h1>
        <Link to="/">Back to Game</Link>
        <hr />
        {results.length === 0 ? (
          <p>No results found.</p>
        ) : (
          <div style={{ overflowX: 'auto', width: '100%' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid black' }}>
                  <th style={{ padding: '8px' }}>Board Size</th>
                  <th style={{ padding: '8px' }}>Opponent</th>
                  <th style={{ padding: '8px' }}>Winner</th>
                  <th style={{ padding: '8px' }}>Total Moves</th>
                  <th style={{ padding: '8px' }}>Date</th>
                  <th style={{ padding: '8px' }}>Replay</th>
                </tr>
              </thead>
              <tbody>
                {results.map((result) => (
                  <tr key={result.gameSessionId} style={{ borderBottom: '1px solid #ccc' }}>
                    <td style={{ padding: '8px', textAlign: 'center' }}>{result.boardSize}x{result.boardSize}</td>
                    <td style={{ padding: '8px', textAlign: 'center' }}>{result.opponentType}</td>
                    <td style={{ padding: '8px', textAlign: 'center' }}>{result.winner || 'DRAW'}</td>
                    <td style={{ padding: '8px', textAlign: 'center' }}>{result.totalMoves}</td>
                    <td style={{ padding: '8px', textAlign: 'center' }}>
                      {new Date(result.completedAt).toLocaleString()}
                    </td>
                    <td style={{ padding: '8px', textAlign: 'center' }}>
                      <Link to={`/replay/${result.gameSessionId}`}>
                        View Replay
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default ResultBoard;