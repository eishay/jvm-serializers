function glue(chartUpdateFunc, categories, entries, filterInput, filterError, filterPermalink) {
  var allFalse = []
  var allTrue = []
  for (var ei = 0; ei < entries.length; ei++) {
    allFalse.push(false)
    allTrue.push(true)
  }

  // properties :: Map[String,Array[Bool]]
  // properties[p][ei] == true iff result entry 'ei' has the property 'p'
  var propertyMasks = {}
  for (var ci = 0; ci < categories.length; ci++) {
    var category = categories[ci]
    for (var pi = 0; pi < category.properties.length; pi++) {
      var name = category.properties[pi].name
      if (propertyMasks.hasOwnProperty(name)) {
        throw Error("property " + JSON.stringify(name) + " defined multiple times")
      }
      propertyMasks[name] = allFalse.slice(0)
    }
  }
  for (var ei = 0; ei < entries.length; ei++) {
    var entry = entries[ei]
    for (var pi = 0; pi < entry.properties.length; pi++) {
      var property = entry.properties[pi]
      if (!propertyMasks.hasOwnProperty(property)) {
        throw Error("entry " + JSON.stringify(entry.name) + " lists an unknown property " + JSON.stringify(property))
      }
      propertyMasks[property][ei] = true
    }
    // Add the entry name as a property as well
    if (propertyMasks.hasOwnProperty(entry.name)) {
      throw Error("entry name " + JSON.stringify(entry.name) + " conflicts with a property name or other entry name")
    }
    var mask = allFalse.slice(0)
    mask[ei] = true
    propertyMasks[entry.name] = mask
  }

  // Converts binary functions into array binary functions.
  // The left-hand array is mutated.
  function liftArrayBinary(f) {
    return function(a, b) {
      for (var i = 0; i < a.length; i++) {
        a[i] = f(a[i], b[i])
      }
      return a
    }
  }

  // Converts unary functions into array binary functions.
  // The array is mutated.
  function liftArrayUnary(f) {
    return function(a) {
      for (var i = 0; i < a.length; i++) {
        a[i] = f(a[i])
      }
      return a
    }
  }

  var evalDomain = {
    '&': liftArrayBinary(function(a, b) { return a && b }),
    '|': liftArrayBinary(function(a, b) { return a || b }),
    '-': liftArrayBinary(function(a, b) { return a && !b }),
    '!': liftArrayUnary(function(a) { return !a }),
    ident: function(s) {
      if (propertyMasks.hasOwnProperty(s)) {
        return propertyMasks[s].slice(0)  // Return a copy since the operations mutate the array.
      } else {
        return null
      }
    }
  }

  function applyFilterToCharts() {
    while (filterError.firstChild) filterError.removeChild(filterError.firstChild)

    expr = filterInput.value
    if (expr.trim() === '') {
      // If there's no expression, show all serializers.
      filterPermalink.href = "#"
      chartUpdateFunc(allTrue)
    }
    else {
      filterPermalink.href = "#filter=" + encodeURIComponent(expr)
      try {
        enabled = beval.eval(expr, evalDomain)
        chartUpdateFunc(enabled)
      }
      catch (ex) {
        if (ex instanceof beval.EvalError) {
          filterError.appendChild(document.createTextNode(ex.toString()))
        } else {
          throw ex
        }
      }
    }
  }

  // See if there's a pre-configured filter expression in the URL fragment.
  if (location.hash.length > 0) {
    hashParts = location.hash.substring(1).split('&')
    for (var i = 0; i < hashParts.length; i++) {
      var part = hashParts[i]
      if (part.lastIndexOf("filter=", 0) === 0) {
        filterInput.value = decodeURIComponent(part.substring("filter=".length))
        break
      }
    }
  }

  applyFilterToCharts()

  function onKeyPress(ev) {
    if (ev.keyCode === 13) {  // TODO: better way to do this than hard-coding 13?
      applyFilterToCharts()
      location.hash = ''
    }
  }
  filterInput.addEventListener("keypress", onKeyPress, false)
}
