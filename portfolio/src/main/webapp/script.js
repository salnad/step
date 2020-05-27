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

// CODE FOR THE CAROUSEL

var curr_item = -1; // store current page number

// moves to next item in carousel (gets list of items from html, populates the current item + 1th element in the list to the content div)
function carousel_next() {
    var carousel_items = document.getElementById("carousel-items").getElementsByTagName("li");
    var carousel = document.getElementById("carousel-content");
    curr_item = (curr_item + 1) % carousel_items.length;
    carousel.innerHTML = carousel_items[curr_item].innerHTML;    
}

// moves to next item in carousel (gets list of items from html, populates the current item - 1th element in the list to the content div)
function carousel_prev() {
    var carousel_items = document.getElementById("carousel-items").getElementsByTagName("li");
    var carousel = document.getElementById("carousel-content");
    curr_item = (carousel_items.length + curr_item - 1) % carousel_items.length;
    carousel.innerHTML = carousel_items[curr_item].innerHTML;    
}

// loads the first element in the list in the content on the page loading
window.onload = carousel_next;