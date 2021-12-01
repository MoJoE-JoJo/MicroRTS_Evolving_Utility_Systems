// var path = "C:\Users\Johan\Documents\GitHub\MicroRTS_EUS\nevt\fitness\fitness.xml"

var xAxis = {
  title: {
    text: 'Generation',
    font: {
      family: 'Courier New, monospace',
      size: 18,
      color: '#7f7f7f'
    }
  }
}

var yAxis = {
  title: {
    text: 'Fitness',
    font: {
      family: 'Courier New, monospace',
      size: 18,
      color: '#7f7f7f'
    }
  }
}

var boxPlotLayout = {
    title: {
      text:'Box plot',
      font: {
        family: 'Courier New, monospace',
        size: 24
      },
      xref: 'paper',
      x: 0.05,
    },
    xaxis: xAxis,
    yaxis: yAxis
}

const lineGraphLayout = {
  title: {
    text:'Line graph',
    font: {
      family: 'Courier New, monospace',
      size: 24
    },
    xref: 'paper',
    x: 0.05,
  },
  xaxis: xAxis,
  yaxis: yAxis
};

const createBoxPlotData = (json) => {
    const data = []
    json.run[0].generation.forEach(generation => {
        const y = []
        generation.specie.forEach(specie => {
          specie.chromosome.forEach(chromosome => {
            y.push(chromosome._attr.fitness._value)
          })
        })
        let sum = 0;
        y.forEach(v => {
          sum += v
        })
        data.push({
            y: y,
            type: 'box',
            name: generation._attr.id._value,
            average: sum / y.length
        })
    })
    return data
}

const createLineGraphData = (json) => {
    const data = []
    const yMax = []
    const xMax = []
    const yAvg = []
    const xAvg = []
    for (let i = 0; i < json.run[0].generation.length; i++) {
      const fitness = json.run[0].generation[i].fitness[0]
      yMax.push(fitness.max[0]._text)
      xMax.push(i)
      yAvg.push(fitness.avg[0]._text)
      xAvg.push(i)
    }
    data.push({
      x: xMax,
      y: yMax,
      mode: 'lines',
      name: 'Max',
      line: {
        dash: 'solid',
        width: 2
      }
    })
    data.push({
      x: xAvg,
      y: yAvg,
      mode: 'lines',
      name: 'Average',
      line: {
        dash: 'solid',
        width: 2
      }
    })
    return data
}

document.getElementById('inputfile')
    .addEventListener('change', function() {
        
    var fr=new FileReader()
    fr.onload=function(){
        result = fr.result
        const json = xmlToJSON.parseString(result)
        // console.log(json)
        const boxPlotData = createBoxPlotData(json)
        const lineGraphData = createLineGraphData(json)
        // console.log(lineGraphData)
        Plotly.newPlot('box-plot', boxPlotData, boxPlotLayout)
        Plotly.newPlot('line-graph', lineGraphData, lineGraphLayout)
    }
        
    fr.readAsText(this.files[0])
})