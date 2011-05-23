using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

/// <summary>
/// Summary description for GraphGenerator
/// </summary>
public class GraphGenerator
{
    const int INF = 2147483647;

    /// <summary>
    /// Generuje graf pełny z krawędziami skierowanymi o losowych wagach
    /// </summary>
    /// <param name="nodes">ilość wierzchołków</param>
    /// <param name="minWeight">minimalna wartość wagi krawędzi</param>
    /// <param name="maxWeight">maksymalna wartość wagi krawędzi</param>
    /// <returns>Macierz sąsiedztwa</returns>
    public double[,] GenerateCompleteGraph(int nodes, int minWeight, int maxWeight)
    {
        double[,] matrix = new double[nodes, nodes];
        Random random = new Random();

        for (int i = 0; i < nodes; i++)
        {
            for (int j = 0; j < nodes; j++)
            {
                if (i == j)
                {
                    matrix[i, j] = INF;
                }
                else
                {
                    matrix[i, j] = random.Next(minWeight, maxWeight);
                }
            }
        }

        //PrintMatrix(matrix, nodes);

        return matrix;
    }

    /// <summary>
    /// Generuje graf z krawędziami skierowanymi o losowych wagach.
    /// Wystąpienie konkretnej krawędzi określa podane prawdopodobieństwo
    /// </summary>
    /// <param name="pr">Prawdopodobieństwo wystąpienia krawędzi</param>
    /// <param name="nodes">Ilość wierzchołków w grafie</param>
    /// <param name="minWeight">Minimalna wartość wag krawędzi</param>
    /// <param name="maxWeight">Maksymalna wartość wag krawędzi</param>
    /// <returns>Macierz sąsiedztwa</returns>
    public double[,] GenerateGraph(int pr, int nodes, int minWeight, int maxWeight)
    {
        double[,] matrix = new double[nodes, nodes];
        Random random = new Random();

        for (int i = 0; i < nodes; i++)
        {
            for (int j = 0; j < nodes; j++)
            {
                if (i == j)
                {
                    matrix[i, j] = INF;
                }
                else
                {
                    if (random.Next(0, 100) <= pr)
                    {
                        matrix[i, j] = random.Next(minWeight, maxWeight);
                    }
                    else
                    {
                        matrix[i, j] = INF;
                    }
                }
            }
        }

        //PrintMatrix(matrix, nodes);

        return matrix;
    }

    /// <summary>
    /// Tworzy tabele-macierz sasiedztwa
    /// </summary>
    /// <param name="matrix">macierz sasiedztwa grafu</param>
    /// <returns>Tabela-macierz sasiedztwa</returns>
    public Table PrintMatrix(double[,] matrix)
    {
        if (matrix == null)
        {
            Table tblx = new Table();
            TableRow trx = new TableRow();
            TableCell tcx = new TableCell();
            Label labelx = new Label();
            labelx.Text = "Nie można było wygenerować macierzy";
            tcx.Controls.Add(labelx);
            trx.Cells.Add(tcx);
            tblx.Rows.Add(trx);
            return tblx;
        }

        int nodesConut = matrix.GetLength(1);

        Table tbl = new Table();
        tbl.BorderWidth = 1;
        
        TableRow trCol = new TableRow();
        trCol.BorderWidth = 1;

        for (int j = 0; j < nodesConut+1; j++)
        {
            TableCell tc = new TableCell();
            tc.BorderWidth = 1;
            tc.HorizontalAlign = HorizontalAlign.Center;
            Label label = new Label();

            if (j == 0)
            {
                label.Text = "z\\do";
            }
            else
            {
                label.Text = "<b>" + j.ToString() + "</b>";
            }

            tc.Controls.Add(label);
            trCol.Cells.Add(tc);
        }
        tbl.Rows.Add(trCol);

        for (int i = 0; i < nodesConut; i++)
        {
            TableRow tr = new TableRow();
            tr.BorderWidth = 1;

            TableCell tcRow = new TableCell();
            tcRow.BorderWidth = 1;
            tcRow.HorizontalAlign = HorizontalAlign.Center;
            Label labelRow = new Label();
            labelRow.Text = "<b>" + (i+1).ToString() + "</b>";
            tcRow.Controls.Add(labelRow);
            tr.Cells.Add(tcRow);

            for (int j = 0; j < nodesConut; j++)
            {
                TableCell tc = new TableCell();
                tc.BorderWidth = 1;
                tc.Height = 35;
                tc.Width = 35;
                tc.HorizontalAlign = HorizontalAlign.Center;
                Label label = new Label();

                if (matrix[i, j] == 2147483647)
                {
                    label.Text = "-";
                }
                else
                {
                    label.Text = matrix[i, j].ToString();
                }
                tc.Controls.Add(label);
                tr.Cells.Add(tc);
            }
            tbl.Rows.Add(tr);
        }

        return tbl;
    }
}
