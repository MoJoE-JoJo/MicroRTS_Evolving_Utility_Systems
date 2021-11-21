// var path = "C:\Users\Johan\Documents\GitHub\MicroRTS_EUS\nevt\fitness\fitness.xml"

var layout = {
    title: {
      text:'Box plot',
      font: {
        family: 'Courier New, monospace',
        size: 24
      },
      xref: 'paper',
      x: 0.05,
    },
    xaxis: {
      title: {
        text: 'Generation',
        font: {
          family: 'Courier New, monospace',
          size: 18,
          color: '#7f7f7f'
        }
      },
    },
    yaxis: {
      title: {
        text: 'Fitness',
        font: {
          family: 'Courier New, monospace',
          size: 18,
          color: '#7f7f7f'
        }
      }
    }
}

const createBoxPlotData = (json) => {
    const data = []
    json.run[0].generation.forEach(generation => {
        const y = []
        generation.specie[0].chromosome.forEach(chromosome => {
            console.log(chromosome._attr.fitness._value)
            y.push(chromosome._attr.fitness._value)
        })
        data.push({
            y: y,
            type: 'box',
            name: 'generation ' + generation._attr.id._value
        })
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
        const data = createBoxPlotData(json)
        // console.log(data)
        Plotly.newPlot('box-plot', data, layout)
    }
        
    fr.readAsText(this.files[0])
})