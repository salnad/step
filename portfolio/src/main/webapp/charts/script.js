google.charts.load('current', {'packages': ['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('string', 'Tasks');
  data.addColumn('number', 'Hours Spent');
  data.addRows([
    ['Sleep', 7.5],
    ['Watch Youtube/TikTok', 5],
    ['Work', 8],
    ['Eat / Maintain Myself', 0.5],
    ['Walk Outside', 1],
    ['Talk to Other People', 2]
  ]);
  
  const options = {
    'title': 'Daily Activity Breakdown',
    'width': 500,
    'height': 500,
  };
  
  const chart = new google.visualization.PieChart(
    document.getElementById('chart-container')
  );

  chart.draw(data, options);
}

