/* 
CODE FOR GAME OF LIFE
    The game of life is a "cellular automota" game, where a grid of cells which can either be "alive" or "dead".
    Each game step, all the cells are updated based upon the amount live cells around them (either becoming alive, staying alive, or dying).
    This code contains the logic for the game of life, and displays it in a canvas element. 
*/

// Animation specific variables
let canvas = document.getElementById("mycanvas");
let ctx = canvas.getContext("2d");
let stop = true;
let then, elapsed;

resizeCanvasToDisplaySize(canvas);

// Game of Life specific variables
const SPEED = 200; // in miliseconds (speed between updates)
const CELL_SIZE = 10; // in pixels (side length of individual cell)
const SEED_SPAWN_RATE = 0.05; // as a ratio (likelihood a cell is 'alive' when seeded)

let rows = Math.floor(canvas.height / CELL_SIZE);
let cols = Math.floor(canvas.width / CELL_SIZE);

// Declare grid for Game of Life
let grid = new Array(rows);
for (let i = 0; i < grid.length; i++) {
	grid[i] = new Array(cols);
}

// Animation "on/off" function (toggles whether game of life is running or not)
function toggleGameOfLifeCycle() {
    resizeCanvasToDisplaySize(canvas);
    if (stop) {
        stop = false;
        startGameOfLifeCycle();
    } else {
        stop = true;
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    }
}

function startGameOfLifeCycle() {
    seedGrid();
    drawGrid();
    
    then = Date.now();
    startTime = then;
    gameOfLifeCycle();
}

function gameOfLifeCycle() {
    if (stop) {
        return;
    }
    
    requestAnimationFrame(gameOfLifeCycle);
    let now = Date.now();
    elapsed = now - then;

    if (elapsed > SPEED) {
        updateGrid();
        drawGrid();
        then = Date.now();
    }
}

// Game of Life specific helper functions
function seedGrid() {
    for (let x = 0; x < cols; x++) {
        for (let y = 0; y < rows; y++) {
            if (Math.random() < SEED_SPAWN_RATE) {
                grid[y][x] = true;
            }
            else {
                grid[y][x] = false;
            }
        }
    } 
}

function updateGrid() {
  resizeCanvasToDisplaySize(canvas);
  let future = new Array(rows);
  for (let i = 0; i < future.length; i++) {
    future[i] = new Array(cols);
    for (let j = 0; j < cols; j++) {
        future[i][j] = false;
    }
  }
  
  for (let x = 1; x < cols - 1; x++) {
  	for (let y = 1; y < rows - 1; y++) {
    	let live_count = 0;
    	let is_alive = grid[y][x] === true;
      for (let i = -1; i <= 1; i++) {
      	for (let j = -1; j <= 1; j++) {
        	if (grid[y + j][x + i] === true) live_count++;
        }
      }
      
      if (is_alive) live_count--;
      
      if (is_alive && (live_count === 2 || live_count === 3)) 
      	future[y][x] = true;
     	else if (!is_alive && live_count >= 3) 
      	future[y][x] = true;
      else 
      	future[y][x] = false;
    }
  }
  
  for (let x = 0; x < cols; x++) {
    for (let y = 0; y < rows; y++) {
			grid[y][x] = future[y][x];
		}
  } 
}

function drawGrid() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.fillStyle = "rgba(20, 20, 20, 1)";
    for (let x = 0; x < cols; x++) {
        for (let y = 0; y < rows; y++) {
        if (grid[y][x] === true)
            ctx.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    } 
}

// For styling (ensures canvas resolution is same as webpage)
function resizeCanvasToDisplaySize(canvas) {
   // look up the size the canvas is being displayed
   const width = canvas.clientWidth;
   const height = canvas.clientHeight;

   // If it's resolution does not match change it
   if (canvas.width !== width || canvas.height !== height) {
     canvas.width = width;
     canvas.height = height;
     return true;
   }

   return false;
}
