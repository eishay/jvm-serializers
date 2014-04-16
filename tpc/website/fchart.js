var fchart = (function() {

function Group(columns, entries) {
  var updateFuncs = []

  this.create = function(spec) {
    var chart = createChart(spec, columns, entries)
    updateFuncs.push(chart.updateFunc)
    return chart.div
  }

  this.update = function(enabled) {
    for (var i = 0; i < updateFuncs.length; i++) {
      updateFuncs[i](enabled)
    }
  }
}

function createChart(spec, columns, entries) {
  var chartDiv = document.createElement("div")
  chartDiv.className = "fchart"

  function getColumnIndex(columnName) {
    var columnIndex = columns.indexOf(columnName)
    if (columnIndex < 0) {
      throw new Error("chart spec references unknown column " + JSON.stringify(columnName))
    }
    return columnIndex
  }

  var renderer
  if (typeof spec === "string") {
    renderer = new SimpleRenderer(getColumnIndex(spec))
  }
  else if (spec instanceof Array) {
    if (spec[0] === "overlay") {
      columnNames = spec.slice(1)
      columnIndexes = []
      for (var dsi = 1; dsi < spec.length; dsi++) {
        columnIndexes.push(getColumnIndex(spec[dsi]))
      }
      renderer = new OverlayRenderer(columnIndexes)
    }
    else {
      throw new Error("couldn't understand chart spec: " + JSON.stringify(spec))
    }
  }
  else {
    throw new Error("couldn't understand chart spec: " + JSON.stringify(spec))
  }

  var rows = []

  for (var ei = 0; ei < entries.length; ei++) {
    var entry = entries[ei]
    
    var chartRow = document.createElement("div"); chartDiv.appendChild(chartRow)
    chartRow.className = "fchart-row"
    
    var chartLabel = document.createElement("div"); chartRow.appendChild(chartLabel)
    chartLabel.className = "fchart-row-label"
    chartLabel.appendChild(document.createTextNode(entry.name))

    var chartData = document.createElement("div"); chartRow.appendChild(chartData)
    chartData.className = "fchart-row-data"

    var chartBar = renderer.render(chartData)

    rows.push({div: chartRow, bar: chartBar})
  }

  function updateFunc(enabled) {
    // Loop through to find the max value.
    var max = 0
    for (var ei = 0; ei < entries.length; ei++) {
      if (enabled[ei]) {
        var val = renderer.getVal(entries[ei].results)
        if (val > max) {
          max = val
        }
      }
    }

    if (max === 0) max = 1  // Avoid divide-by-zero

    // - Adjust all entry widths to be relative to the max.
    // - Hide/show entries depending on whether they're enabled.
    for (var ei = 0; ei < entries.length; ei++) {
      var row = rows[ei]
      renderer.update(max, entries[ei].results, row.bar)
      if (enabled[ei]) {
        row.div.className = "fchart-row"
      } else {
        row.div.className = "fchart-row-hidden"
      }
    }
  }

  // Initialize everything to visisble.
  var allTrue = []
  for (var ei = 0; ei < entries.length; ei++) {
    allTrue.push(true)
  }
  updateFunc(allTrue)

  return {
    div: chartDiv,
    updateFunc: updateFunc
  }
}

function SimpleRenderer(columnIndex) {
  this.render = function(container) {
    var bar = document.createElement("div"); container.appendChild(bar)
    bar.className = "fchart-row-data-bar"
    return bar
  }

  this.getVal = function(results) {
    return results[columnIndex]
  }

  this.update = function(chartMax, results, bar) {
    var val = results[columnIndex]
    var percent = val * 100 / chartMax
    bar.style.width = percent.toString() + "%"
  }
}

function OverlayRenderer(columnIndexes) {
  this.render = function(container) {
    var bars = []
    for (var i = 0; i < columnIndexes.length; i++) {
      var bar = document.createElement("div"); container.appendChild(bar)
      bar.className = "fchart-row-data-bar fchart-row-data-bar-" + i.toString()
      bars.push(bar)
    }
    return bars
  }

  this.getVal = function(results) {
    var max = 0
    for (var i = 0; i < columnIndexes.length; i++) {
      var val = results[columnIndexes[i]]
      max = Math.max(max, val)
    }
    return max
  }

  this.update = function(chartMax, results, bars) {
    var prevBarEnd = 0
    for (var i = 0; i < columnIndexes.length; i++) {
      var val = results[columnIndexes[i]]
      var barSize = Math.max(0, val - prevBarEnd)
      var percent = barSize * 100 / chartMax
      bars[i].style.width = percent.toString() + "%"
      prevBarEnd = Math.max(prevBarEnd, val)
    }
  }
}

return {
  Group: Group
}

})()
