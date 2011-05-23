using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class _Default : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {

    }

    protected void btnGenerate_Click(object sender, EventArgs e)
    {
        if (Page.IsValid)
        {
            GraphGenerator graphGenerator = new GraphGenerator();
            double[,] matrix;

            string graphType = ddlGraphType.SelectedValue.ToString();

            if (graphType == "notcomplete")
            {
                matrix = graphGenerator.GenerateGraph(Int32.Parse(tbEdgeProb.Text), Int32.Parse(tbNodeCount.Text), Int32.Parse(tbMinWeight.Text), Int32.Parse(tbMaxWeight.Text));
            }
            else
            {
                matrix = graphGenerator.GenerateCompleteGraph(Int32.Parse(tbNodeCount.Text), Int32.Parse(tbMinWeight.Text), Int32.Parse(tbMaxWeight.Text));
            }

            Table tbl = graphGenerator.PrintMatrix(matrix);
            pnlMatrix.Controls.Add(tbl);
            Session["matrix"] = matrix;
        }
    }

    protected void ddlGraphType_SelectedIndexChanged(object sender, EventArgs e)
    {
        string graphType = ddlGraphType.SelectedValue.ToString();

        if (graphType == "notcomplete")
        {
            pnlProb.Visible = true;
        }
        else
        {
            pnlProb.Visible = false;
        }
    }

    protected void RadioButton1_CheckedChanged(object sender, EventArgs e)
    {
        pnlLoad.Enabled = true;
        pnlGenerate.Enabled = false;
    }

    protected void RadioButton2_CheckedChanged(object sender, EventArgs e)
    {
        pnlLoad.Enabled = false;
        pnlGenerate.Enabled = true;
    }

    protected void btnStart_Click(object sender, EventArgs e)
    {
        if (Session["matrix"] != null)
        {
            double[,] matrix = (double[,])Session["matrix"];
            string algType = RadioButtonList1.SelectedValue.ToString();
            GraphGenerator graphGenerator = new GraphGenerator();
            TspAlgorithm tspAlgorithm = new TspAlgorithm();
            Random random = new Random();

            int startNode = random.Next() % matrix.GetLength(1);
            if (String.IsNullOrEmpty(tbStartNode.Text) == false)
            {
                startNode = Int32.Parse(tbStartNode.Text) - 1;
            }

            Table tbl = graphGenerator.PrintMatrix(matrix);
            pnlMatrix.Controls.Add(tbl);
            List<object> results = new List<object>();

            if (algType == "0")
            {
                results = tspAlgorithm.GetPermutationAlg(matrix, startNode);
            }
            else if (algType == "1")
            {
                results = tspAlgorithm.GetBBAlg(matrix, startNode);
            }
            else if (algType == "2")
            {
                results = tspAlgorithm.GreedyAlg(matrix, startNode);
            }

            string htmlCode = "";

            if (results[3].ToString() == "noresult")
            {
                htmlCode = "czas wykonywania: " + results[2] + "<br />" +
                            "wierzcholek startowy: " + (startNode + 1).ToString() + "<br />" +
                            "długość ścieżki: brak rozwiazania <br />" +
                            "kolejność wierzchołków: brak rozwiazania ";
            }
            else
            {
                htmlCode = "czas wykonywania: " + results[2] + "<br />" +
                            "wierzcholek startowy: " + (startNode + 1).ToString() + "<br />" +
                            "długość ścieżki: " + results[0] + "<br />" +
                            "kolejność wierzchołków: " + results[1];
            }
            LiteralControl control = new LiteralControl(htmlCode);
            pnlResults.Controls.Add(control);

            btnGenerateXML.Visible = true;
        }
    }

    protected void btnLoad_Click(object sender, EventArgs e)
    {
        HttpPostedFile file = FileUpload1.PostedFile;
        //string extension = System.IO.Path.GetExtension(file.FileName);

        if ((file != null) && (file.ContentLength > 0))
        {
            string type = file.ContentType;
            type = type.Split('/')[1];

            if (type.Equals("xml"))
            {
                XMLMachine xmlMachine = new XMLMachine();
                //double[,] matrix = xmlMachine.GenerateMatrixFromXML(file);
                System.Xml.XmlReader reader = System.Xml.XmlReader.Create(file.InputStream);

                var g = xmlMachine.ReadFromGraphML(reader);
                double[,] matrix = xmlMachine.GenerateMatrixFromGraphML(g);
                Session["g"] = g;

                GraphGenerator graphGenerator = new GraphGenerator();
                Table tbl = graphGenerator.PrintMatrix(matrix);
                pnlMatrix.Controls.Add(tbl);
                Session["matrix"] = matrix;
                //string saveLocation = Server.MapPath("Avatars\\Users\\" + getSHA1(Request.QueryString["user"]));
                //try
                //{
                //    file1.SaveAs(saveLocation);
                //    lblMessage.Text = "Plik został zapisany na serwerze";
                //    btnDeleteAvatar.Visible = true;
                //    AspPanel4.Visible = true;
                //    UserAvatar.Visible = true;
                //    UserAvatar.ImageUrl = "Avatars/Users/" + getSHA1(Request.QueryString["user"]);

                //    UserAvatarProfil.Visible = true;
                //    UserAvatarProfil.ImageUrl = "Avatars/Users/" + getSHA1(Request.QueryString["user"]);

                //    if (UserAvatar.Height.Value > UserAvatar.Width.Value)
                //    {
                //        UserAvatar.Height = 120;
                //        UserAvatarProfil.Height = 100;
                //    }
                //    else
                //    {
                //        UserAvatar.Width = 120;
                //        UserAvatarProfil.Width = 100;
                //    }
                //}
                //catch (Exception ex)
                //{
                //    lblMessage.Text = "Wystąpił błąd: " + ex.Message;
                //    AspPanel4.Visible = true;
                //}
            }
            else
            {
                lblXML.Text = "To nie jest plik XML";
            }
        }
        else
        {
            lblXML.Text = "Nie podałeś lokalizacji pliku na dysku";
        }
    }

    protected void btnGenerateXML_Click(object sender, EventArgs e)
    {
        Response.Clear();

        Response.ContentType = "text/xml";
        Response.AppendHeader("Content-Disposition", "attachment; filename=graph.xml");

        double[,] matrix = Session["matrix"] as double[,];

        XMLMachine xmlMachine = new XMLMachine();

        xmlMachine.WriteGraphMLToStream(xmlMachine.GenerateGraphFromMatrix(matrix), Response.OutputStream);

        //ds.WriteXml(Response.OutputStream, XmlWriteMode.IgnoreSchema);

        Response.End();
    }
}
