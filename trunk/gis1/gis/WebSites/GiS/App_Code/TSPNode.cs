using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using System.Xml.Serialization;

/// <summary>
/// Summary description for TSPNode
/// </summary>
public class TSPNode
{
    public string id { set; get; }
    [XmlAttribute("label")]
    public string label { set; get; }
    [XmlAttribute("color")]
    public string color { set; get; }
    [XmlAttribute("2D")]
    public string _2D { set; get; }
    [XmlAttribute("3D")]
    public string _3D { set; get; }
    [XmlAttribute("nD")]
    public string nD { set; get; }

    public TSPNode(string id)
    {
        this.id = id;
    }

    public TSPNode(int id)
    {
        this.id = id.ToString();
    }

    public TSPNode()
    {
    }
}