google.charts.load('current', {'packages': ['corechart']});
google.charts.setOnLoadCallback(drawChart);

async function drawChart() {
  const response = await fetch('/pokedex-data');
  const generationArray = await response.json();
  const data = new google.visualization.DataTable();

  data.addColumn('string', 'Generation');
  data.addColumn('number', 'Amount of Pokemon');
  for (let i = 0; i < generationArray.length; i++) {
    if (generationArray[i] !== 0) {
      data.addRow([i.toString(), generationArray[i]]);
    }
  }

  const options = {
    'title': 'Pokemon Count per Generation',
    'width': 500,
    'height': 500,
  };
  
  const chart = new google.visualization.PieChart(
    document.getElementById('chart-container')
  );

  chart.draw(data, options);
}

