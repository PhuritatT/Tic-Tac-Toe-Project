import Cell from './Cell';

function GameBoard({ session, onMakeMove, isLoading }) {
  const { boardSize, currentBoardState, gameStatus } = session;
  
  const boardArray = currentBoardState.split(',');

  const boardStyle = {
    display: 'grid',
    gridTemplateColumns: `repeat(${boardSize}, 100px)`,
    gridTemplateRows: `repeat(${boardSize}, 100px)`,
    width: `${boardSize * 100}px`,
    margin: 'auto',
  };

  const handleClick = (index) => {
    if (isLoading || gameStatus !== 'IN_PROGRESS' || boardArray[index] !== 'E') {
      return;
    }
    
    const rowIndex = Math.floor(index / boardSize);
    const colIndex = index % boardSize;
    
    onMakeMove(rowIndex, colIndex);
  };

  return (
    <div style={boardStyle} className="game-board">
      {boardArray.map((value, index) => (
        <Cell 
          key={index}
          value={value === 'E' ? '' : value}
          onClick={() => handleClick(index)}
        />
      ))}
    </div>
  );
}

export default GameBoard;