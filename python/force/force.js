
var w = 1280,
    h = 800;

var vis = d3.select("#chart")
  .append("svg:svg")
    .attr("width", w)
    .attr("height", h);

d3.json("./force.json", function(json) {
  var force = d3.layout.force()
      .charge(-400)
      .linkDistance(260)
      .nodes(json.nodes)
      .links(json.links)
      .size([w, h])
      .on("tick", tick)
      .start();

  var svg = d3.select("body").append("svg")
      .attr("width", w)
      .attr("height", h);

  // build the arrow.
  svg.append("svg:defs").selectAll("marker")
      .data(["end"])      // Different link/path types can be defined here
    .enter().append("svg:marker")    // This section adds in the arrows
      .attr("id", String)
      .attr("viewBox", "0 -5 10 10")
      .attr("refX", 15)
      .attr("refY", -1.5)
      .attr("markerWidth", 6)
      .attr("markerHeight", 6)
      .attr("orient", "auto")
    .append("svg:path")
      .attr("d", "M0,-5L10,0L0,5");

    // add the links and the arrows
  var path = svg.append("svg:g").selectAll("path")
      .data(force.links())
    .enter().append("svg:path")
  //    .attr("class", function(d) { return "link " + d.type; })
      .attr("class", "link")
      .attr("marker-end", "url(#end)");

  // define the nodes
  var node = svg.selectAll(".node")
      .data(force.nodes())
    .enter().append("g")
      .attr("class", "node")
      .call(force.drag);

  // add the nodes
  node.append("circle")
      .attr("r", 5);

      // add the text
    node.append("text")
        .attr("x", 12)
        .attr("dy", ".35em")
        .text(function(d) { return d.name; });


        // add the curvy lines
      function tick() {
          path.attr("d", function(d) {
              var dx = d.target.x - d.source.x,
                  dy = d.target.y - d.source.y,
                  dr = Math.sqrt(dx * dx + dy * dy);
              return "M" +
                  d.source.x + "," +
                  d.source.y + "A" +
                  dr + "," + dr + " 0 0,1 " +
                  d.target.x + "," +
                  d.target.y;
          });

          node.attr("transform", function(d) {
        	    return "translate(" + d.x + "," + d.y + ")"; });
      }
});
