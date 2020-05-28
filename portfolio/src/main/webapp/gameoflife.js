// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// CODE FOR GAME OF LIFE

// Page specific variables
var canvas = document.getElementById("mycanvas");
var ctx = canvas.getContext("2d");
var frameID;

resizeCanvasToDisplaySize(canvas);

// GOL specific variables
const SPEED = 200; // in miliseconds
const CELL_SIZE = 10; // in pixels
const SEED_SPAWN_RATE = 0.1; // as a ratio

var rows = Math.floor(canvas.height / CELL_SIZE);
var cols = Math.floor(canvas.width / CELL_SIZE);

// Declare grid for GOL
var grid = new Array(rows);
for (var i = 0; i < grid.length; i++) {
	grid[i] = new Array(cols);
}


// GOL specific helper functions
function seedGrid() {
    for (var x = 0; x < cols; x++) {
        for (var y = 0; y < rows; y++) {
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
  var future = new Array(rows);
  for (var i = 0; i < future.length; i++) {
    future[i] = new Array(cols);
    for (var j = 0; j < cols; j++) {
        future[i][j] = false;
    }
  }
  
  for (var x = 1; x < cols - 1; x++) {
  	for (var y = 1; y < rows - 1; y++) {
    	var live_count = 0;
    	var is_alive = grid[y][x] === true;
      for (var i = -1; i <= 1; i++) {
      	for (var j = -1; j <= 1; j++) {
        	if (grid[y + j][x + i] === true) live_count++;
        }
      }
      
      if (is_alive) live_count--;
      
      // console.log(`${x}, ${y}: ${live_count}`);
      
      if (is_alive && (live_count === 2 || live_count === 3)) 
      	future[y][x] = true;
     	else if (!is_alive && live_count >= 3) 
      	future[y][x] = true;
      else 
      	future[y][x] = false;
    }
  }
  
  for (var x = 0; x < cols; x++) {
    for (var y = 0; y < rows; y++) {
			grid[y][x] = future[y][x];
		}
  } 
}

function drawGrid() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.fillStyle = "rgba(20, 20, 20, 1)";
    // ctx.fillStyle = "white";
    for (var x = 0; x < cols; x++) {
        for (var y = 0; y < rows; y++) {
        // ctx.strokeRect(x * SQUARE_SIZE, y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        if (grid[y][x] === true)
            ctx.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    } 
}

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

function golLoop() {
    setTimeout(() => {
        updateGrid();
        drawGrid();
        console.log(grid);
        frameID = requestAnimationFrame(golLoop);
    }, SPEED);    
}

seedGrid();
drawGrid();
frameID = requestAnimationFrame(golLoop);

function cancel() {
    cancelAnimationFrame(frameID);
    ctx.clearRect(0, 0, canvas.width, canvas.height);
}