google.charts.load('current', {'packages': ['corechart']});
google.charts.setOnLoadCallback(drawStats);

async function drawStats() {
  const response = await fetch('/pokedex-data');
  const pokeData = await response.json();
  const pokeStats = new google.visualization.DataTable();
  pokeStats.addColumn('string', 'Stat Type');
  pokeStats.addColumn('number', 'Min');
  pokeStats.addColumn('number', '1st Quar');
  pokeStats.addColumn('number', '3rd Quar');
  pokeStats.addColumn('number', 'Max');

  for (stat in pokeData) {
    let quartiles = calculateQuartiles(pokeData[stat]);
    pokeStats.addRow([stat, quartiles['min'], quartiles['firstQ'], quartiles['thirdQ'], quartiles['max']]);
  }

  var chart = new google.visualization.ComboChart(document.getElementById('chart-container'));
  chart.draw(pokeStats, {
    title : 'Box and Whisker Plot of Stats',
    width: 600,
    height: 400,
    vAxis: {title: "Value"},
    series: { 0: {type: "candlesticks"}}
  });
}

function calculateQuartiles(arr) {
  arr = removeNullsAndSort(arr);
  let res = getMedian(arr);
  let medianIdx = res['idx']
  median = res['val'];
  firstQ = getMedian(arr.slice(0,medianIdx))['val'];
  thirdQ = getMedian(arr.slice(medianIdx))['val'];
  return {
    median: median,
    firstQ: firstQ,
    thirdQ: thirdQ,
    min: arr[0],
    max: arr[arr.length - 1]
  };
}

function removeNullsAndSort(arr) {
  arr.sort((a,b) =>{ 
    return a-b
  });
  var i = 0;
  while (i < arr.length) {
    if (arr[i] === 0) {
      arr.splice(i, 1);
    } else {
      ++i;
    }
  }
  return arr;
}

function getMedian(arr) {
  let len = arr.length;
  let mid = Math.floor(len / 2);
  let value;
  if (len % 2 === 0)  {
    value = (arr[mid-1] + arr[mid]) / 2;
  } else {
    value = arr[mid];
  }
  return {
    idx: mid,
    val: value
  };
}
