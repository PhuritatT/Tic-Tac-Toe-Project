import './Cell.css'

function Cell({ value, onClick }) {
  const cellClasses = `cell ${value === 'X' ? 'cell-x' : value === 'O' ? 'cell-o' : ''}`;

  return (
    <div className={cellClasses} onClick={onClick}>
      {value}
    </div>
  );
}

export default Cell;