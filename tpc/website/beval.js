// Boolean expression evaluator.

var beval = (function() {

function eval(s, domain) {
  var stream = new Stream(s)
  var tokenizer = new Tokenizer(stream)

  function expr() {
    var v = term()

    while (true) {
      var t = tokenizer.peek()
      if (t.type === '|' || t.type === '&' || t.type === '-') {
        tokenizer.next()
        var f = domain[t.type]
        v = f(v, term())
      } else {
        return v
      }
    }
  }

  function term() {
    var t = tokenizer.peek()
    if (t.type === '(') {
      tokenizer.next()
      var v = expr()
      var end = tokenizer.peek()
      if (end.type != ')') {
        throw new EvalError(end.pos, "expecting \")\" to match \"(\" at position " + t.pos)
      }
      tokenizer.next()
      return v
    }
    else if (t.type === 'ident') {
      tokenizer.next()
      var v = domain.ident(t.v)
      if (v === null) {
        throw new EvalError(t.pos, "unknown identifier: " + JSON.stringify(t.v))
      }
      return v
    }
    else if (t.type === '!') {
      tokenizer.next()
      var f = domain['!']
      return f(term())
    }
    else {
      throw new EvalError(t.pos, "not expecting: " + t.type)
    }
  }

  var v = expr()
  var t = tokenizer.peek()
  if (t.type != "end of input") {
    throw new EvalError(t.pos, "not expecting: " + t.type)
  }
  return v
}

function EvalError(pos, message) {
  this.pos = pos
  this.message = message

  this.toString = function() {
    return pos.toString() + ": " + message
  }
}

function isIdentChar(c) {
  if (c === null) return false
  return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c === '.' || c == '-' || c == '/'
}

function isDelimiter(c) {
  if (c === null) return false
  return c === '&' || c === '|' || c === '!' || c === '-' || c === '(' || c === ')'
}

function Stream(s) {
  var i = 0
  this.pos = function() { return i+1 }
  this.peek = function() {
    if (i >= s.length) return null
    return s[i]
  }
  this.next = function() {
    if (i >= s.length) return
    i++
  }
}

function Tokenizer(stream) {
  function _next() {
    // Eat whitespace
    while ((c = stream.peek()) === ' ') {
      stream.next()
    }

    if (c === null) return {pos: pos, type: "end of input"}
    var pos = stream.pos()

    if (isDelimiter(c)) {
      stream.next()
      return {pos: pos, type: c}
    }

    if (isIdentChar(c)) {
      stream.next()
      var ident = c
      while (isIdentChar((c = stream.peek()))) {
        stream.next()
        ident += c
      }
      return {pos: pos, type: 'ident', v: ident}
    }

    throw new EvalError(pos, "invalid character: " + JSON.stringify(c))
  }

  var nextToken = _next()
  this.next = function() { nextToken = _next(); }
  this.peek = function() { return nextToken }
}

return {
  eval: eval,
  EvalError: EvalError,
}

})()
