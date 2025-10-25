import { useState, useEffect } from 'react';
import apiClient from './api/api';
import GameBoard from './GameBoard';
import GameSetup from './GameSetup';
import GameInfo from './GameInfo';

import './App.css';

function App() {
  const [gameSession, setGameSession] = useState(null);

  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const existingSessionId = localStorage.getItem('sessionId');
    if (existingSessionId) {
      console.log('Found existing session:', existingSessionId);
      apiClient.get(`/api/games/resume?sessionId=${existingSessionId}`)
        .then(response => {
          setGameSession(response.data);
        })
        .catch(error => {
          console.error('Failed to resume game:', error);
          localStorage.removeItem('sessionId');
        })
        .finally(() => {
          setIsLoading(false);
        });
    } else {
      setIsLoading(false);
    }
  }, []);

  const handleStartGame = (startRequest) => {
    setIsLoading(true);
    apiClient.post('/api/start', startRequest)
      .then(response => {
        const session = response.data;
        setGameSession(session);
        localStorage.setItem('sessionId', session.sessionId);
      })
      .catch(error => {
        console.error('Error starting game:', error);
        alert(error.response?.data?.message || 'Failed to start game');
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleMakeMove = (rowIndex, colIndex) => {
    if (isLoading || gameSession.gameStatus !== 'IN_PROGRESS') return;

    setIsLoading(true);

    const playerToMove = gameSession.nextPlayer;

    const moveRequest = {
      sessionId: gameSession.sessionId,
      rowIndex: rowIndex,
      colIndex: colIndex,
      player: playerToMove
    };

    apiClient.post('/api/games/move', moveRequest)
      .then(response => {
        setGameSession(response.data);
      })
      .catch(error => {
        console.error('Error making move:', error);
        alert(error.response?.data?.message || 'Invalid move!');
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleSurrender = () => {
    if (isLoading || gameSession.gameStatus !== 'IN_PROGRESS') return;

    setIsLoading(true);

    const surrenderRequest = {
      sessionId: gameSession.sessionId,
      player: gameSession.nextPlayer
    };

    apiClient.post('/api/games/surrender', surrenderRequest)
      .then(response => {
        setGameSession(response.data);
      })
      .catch(error => console.error('Error surrendering:', error))
      .finally(() => setIsLoading(false));
  };

  const handleNewGame = () => {
    localStorage.removeItem('sessionId');
    setGameSession(null);
    setIsLoading(false);
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!gameSession) {
    return <GameSetup onStartGame={handleStartGame} />;
  }

  return (
    <div className="game-container">
      <div className="card">
        <GameInfo
          session={gameSession}
          onSurrender={handleSurrender}
          onNewGame={handleNewGame}
        />
        <div className='game-board-card'>
          <GameBoard
            session={gameSession}
            onMakeMove={handleMakeMove}
            isLoading={isLoading}
          />
        </div>
      </div>
    </div>
  );
}

export default App;