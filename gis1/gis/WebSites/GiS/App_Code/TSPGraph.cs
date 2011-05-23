using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Xml.Serialization;

using QuickGraph;
using QuickGraph.Serialization;
using QuickGraph.Serialization.DirectedGraphML;
using QuickGraph.Algorithms;

/// <summary>
/// Summary description for TSPGraph
/// </summary>
public class TSPAdjacencyGraph : AdjacencyGraph<TSPNode, TSPEdge>
{
    [XmlAttribute("name")]
    public string name { get; set; }
    [XmlAttribute("size")]
    public int size { set; get; }
}