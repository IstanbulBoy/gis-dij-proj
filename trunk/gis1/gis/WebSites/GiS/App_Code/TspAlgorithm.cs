using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for TspAlgorithms
/// </summary>
public class TspAlgorithm
{
    public static int INF = 2147483647;
    double bestResult = INF;
    int[] bestPath;
    int[] result;
    BBAlgorithm bbAlg = new BBAlgorithm();

    public List<object> GetBBAlg(double[,] matrix, int startNode)
    {
        result = new int[matrix.GetLength(1)];
        bestPath = new int[matrix.GetLength(1)];

        DateTime ExecutionStartTime;
        DateTime ExecutionStopTime;
        TimeSpan ExecutionTime;

        bbAlg.setupAlg(matrix);

        ExecutionStartTime = DateTime.Now;
        bbAlg.tspPath(startNode);
        GetPermutation(matrix, startNode, 1);
        ExecutionStopTime = DateTime.Now;

        ExecutionTime = ExecutionStopTime.Subtract(ExecutionStartTime);

        List<object> results = new List<object>();
        results.Add(bestResult);
        string bestPathString = "";
        for (int i = 0; i < bestPath.Length; i++)
        {
            bestPathString += (bestPath[i] + 1).ToString();
            bestPathString += ",";
        }
        bestPathString += (bestPath[0] + 1).ToString();
        results.Add(bestPathString);
        results.Add(ExecutionTime.TotalSeconds.ToString() + " sec.");
        //nie znaleziono rozwiazania
        if (bestResult == INF)
        {
            results.Add("noresult");
        }
        else
        {
            results.Add("result");
        }
        return results;
    }

    public List<object> GetBBMeasurement(List<double[,]> matrixList, int startNode, int repeat)
    {
        int count = 0;
        int iterator = 0;
        List<object> results = new List<object>();
        double pathResult = 0;
        double timeResult = 0;

        do
        {
            DateTime ExecutionStartTime;
            DateTime ExecutionStopTime;
            TimeSpan ExecutionTime;

            result = new int[matrixList[iterator].GetLength(1)];
            bestPath = new int[matrixList[iterator].GetLength(1)];
            bestResult = INF;

            bbAlg.setupAlg(matrixList[iterator]);

            ExecutionStartTime = DateTime.Now;

            bbAlg.tspPath(startNode);

            ExecutionStopTime = DateTime.Now;

            ExecutionTime = ExecutionStopTime.Subtract(ExecutionStartTime);
            iterator++;

            if (bestResult != INF)
            {
                pathResult += bestResult;
                timeResult += ExecutionTime.TotalMilliseconds;
                count++;
            }

        } while (count < repeat && iterator != repeat * 2);

        pathResult = pathResult / count;
        timeResult = timeResult / count;

        results.Add(pathResult);
        results.Add(timeResult);
        return results;
    }

    /// <summary>
    /// Zwraca wyniki dla wyliczenia wszystkich permutacji dla TSP grafu
    /// </summary>
    /// <param name="matrix">macierz sasiedztwa grafu</param>
    /// <param name="startNode">wierzcholek poczatkowy</param>
    /// <returns>Lista zawierajaca wyniki</returns>
    public List<object> GetPermutationAlg(double[,] matrix, int startNode)
    {
        result = new int[matrix.GetLength(1)];
        bestPath = new int[matrix.GetLength(1)];

        DateTime ExecutionStartTime;
        DateTime ExecutionStopTime;
        TimeSpan ExecutionTime;

        ExecutionStartTime = DateTime.Now;
        GetPermutation(matrix, startNode, 1);
        ExecutionStopTime = DateTime.Now;

        ExecutionTime = ExecutionStopTime.Subtract(ExecutionStartTime);

        List<object> results = new List<object>();
        results.Add(bestResult);
        string bestPathString = "";
        for (int i = 0; i < bestPath.Length; i++)
        {
            bestPathString += (bestPath[i] + 1).ToString();
            bestPathString += ",";
        }
        bestPathString += (bestPath[0] + 1).ToString();
        results.Add(bestPathString);
        results.Add(ExecutionTime.TotalSeconds.ToString() + " sec.");
        //nie znaleziono rozwiazania
        if (bestResult == INF)
        {
            results.Add("noresult");
        }
        else
        {
            results.Add("result");
        }
        return results;
    }

    public List<object> GetPermutationMeasurement(List<double[,]> matrixList, int startNode, int repeat)
    {
        int count = 0;
        int iterator = 0;
        List<object> results = new List<object>();
        double pathResult = 0;
        double timeResult = 0;

        do
        {
            DateTime ExecutionStartTime;
            DateTime ExecutionStopTime;
            TimeSpan ExecutionTime;

            result = new int[matrixList[iterator].GetLength(1)];
            bestPath = new int[matrixList[iterator].GetLength(1)];
            bestResult = INF;

            ExecutionStartTime = DateTime.Now;
            GetPermutation(matrixList[iterator], startNode, 1);
            ExecutionStopTime = DateTime.Now;

            ExecutionTime = ExecutionStopTime.Subtract(ExecutionStartTime);
            iterator++;

            if (bestResult != INF)
            {
                pathResult += bestResult;
                timeResult += ExecutionTime.TotalMilliseconds;
                count++;
            }

        } while (count < repeat && iterator != repeat * 2);

        pathResult = pathResult / count;
        timeResult = timeResult / count;

        results.Add(pathResult);
        results.Add(timeResult);
        return results;
    }

    private void GetPermutation(double[,] matrix, int startNode, int level)
    {
        int nodesConut = matrix.GetLength(1);

        //startowy wierzcholek trafia od razu do wyniku
        if (level == 1)
        {
            for (int i = 0; i < nodesConut; i++)
            {
                result[i] = -1;
            }
            result[0] = startNode;
        }

        if (level != nodesConut)
        {
            for (int i = 0; i < nodesConut; i++)
            {
                //sprawdza czy wierzcholek jest juz w permutacji
                bool isDuplicate = false;
                for (int j = 0; j < nodesConut - 1; j++)
                {
                    if (result[j] == i)
                    {
                        isDuplicate = true;
                    }
                }
                if (isDuplicate == false)
                {
                    result[level] = i;
                    //obliczylismy pierwsza pozycje permutacji
                    //i nie mozemy jeszcze okreslic wagi krawedzi
                    if (level == 0)
                    {
                        GetPermutation(matrix, 0, level + 1);
                    }
                    //mozemy juz okreslic wage krawedzi
                    else if (matrix[result[level - 1], result[level]] != INF)
                    {
                        //nie okreslilismy jeszcze ostatniego wierzcholka
                        if (level != nodesConut - 1)
                        {
                            GetPermutation(matrix, 0, level + 1);
                        }
                        //ostatni wierzcholek ma droge do startowego wierzcholka
                        else if (level == nodesConut - 1 && matrix[result[level], result[0]] != INF)
                        {
                            GetPermutation(matrix, 0, level + 1);
                        }
                    }
                    result[level] = -1;
                }
            }
        }
        //oblicz wartość permutacji
        else
        {
            double wynik = 0;
            for (int j = 0; j < nodesConut; j++)
            {
                if (j != 0)
                {
                    wynik += matrix[result[j - 1], result[j]];
                }
            }
            wynik += matrix[result[nodesConut - 1], result[0]];

            if (wynik < bestResult)
            {
                bestResult = wynik;
                for (int r = 0; r < result.Length; r++)
                {
                    bestPath[r] = result[r];
                }
            }
        }
    }

    /// <summary>
    /// Zwraca wynik dla wyliczenia algorytmu zachlannego dla TSP grafu
    /// </summary>
    /// <param name="matrix">macierz sasiedztwa grafu</param>
    /// <param name="startNode">wierzcholek poczatkowy</param>
    /// <returns>Lista zawierajaca wyniki</returns>
    public List<object> GreedyAlg(double[,] matrix, int startNode)
    {
        bestPath = new int[matrix.GetLength(1)];

        DateTime ExecutionStartTime;
        DateTime ExecutionStopTime;
        TimeSpan ExecutionTime;

        ExecutionStartTime = DateTime.Now;
        GreedyAlgorithm(matrix, startNode);
        ExecutionStopTime = DateTime.Now;

        ExecutionTime = ExecutionStopTime.Subtract(ExecutionStartTime);

        List<object> results = new List<object>();
        results.Add(bestResult);
        string bestPathString = "";
        for (int i = 0; i < bestPath.Length; i++)
        {
            bestPathString += (bestPath[i] + 1).ToString();
            bestPathString += ",";
        }
        bestPathString += (bestPath[0] + 1).ToString();
        results.Add(bestPathString);
        results.Add(ExecutionTime.TotalSeconds.ToString() + " sec.");
        //nie znaleziono rozwiazania
        if (bestResult == INF)
        {
            results.Add("noresult");
        }
        else
        {
            results.Add("result");
        }
        return results;
    }

    public List<object> GreedyMeasurement(List<double[,]> matrixList, int startNode, int repeat)
    {
        int count = 0;
        int iterator = 0;
        List<object> results = new List<object>();
        double pathResult = 0;
        double timeResult = 0;

        do
        {
            DateTime ExecutionStartTime;
            DateTime ExecutionStopTime;
            TimeSpan ExecutionTime;

            result = new int[matrixList[iterator].GetLength(1)];
            bestPath = new int[matrixList[iterator].GetLength(1)];
            bestResult = INF;

            ExecutionStartTime = DateTime.Now;
            GreedyAlgorithm(matrixList[iterator], startNode);
            ExecutionStopTime = DateTime.Now;

            ExecutionTime = ExecutionStopTime.Subtract(ExecutionStartTime);
            iterator++;

            if (bestResult != INF)
            {
                pathResult += bestResult;
                timeResult += ExecutionTime.TotalMilliseconds;
                count++;
            }

        } while (count < repeat && iterator != repeat * 2);

        pathResult = pathResult / count;
        timeResult = timeResult / count;

        results.Add(pathResult);
        results.Add(timeResult);
        return results;
    }

    private void GreedyAlgorithm(double[,] matrix, int startNode)
    {
        int nodesConut = matrix.GetLength(1);
        int currentNode = startNode;
        double shortestPath = INF;
        int closestNode = -1;
        List<int[]> wrongPath = new List<int[]>();
        bool isWrongPath = true;
        int[] result;

        do
        {
            result = new int[nodesConut];
            for (int i = 0; i < nodesConut; i++)
            {
                result[i] = -1;
            }
            result[0] = startNode;
            currentNode = startNode;
            bestResult = 0;

            for (int i = 0; i < nodesConut; i++)
            {
                //z ostatniego wierzcholka trzeba wrocic do pierwszego
                if (i == nodesConut - 1)
                {
                    if (matrix[currentNode, startNode] != INF)
                    {
                        bestResult += matrix[currentNode, startNode];
                        isWrongPath = false;
                    }
                    else
                    {
                        wrongPath.Add(result);
                        isWrongPath = true;
                    }
                }
                else
                {
                    shortestPath = INF;
                    for (int j = 0; j < nodesConut; j++)
                    {
                        if (matrix[currentNode, j] < shortestPath)
                        {
                            bool isFree = true;
                            //sprawdza czy wierzcholek nie byl juz odwiedzony
                            for (int k = 0; k < nodesConut; k++)
                            {
                                if (result[k] == j)
                                {
                                    isFree = false;
                                }
                            }
                            //jesli nie byl to mozna go odwiedzic
                            if (isFree == true)
                            {
                                result[i + 1] = j;
                                //sprawdz czy proponowana sciezka nie jest na liscie zakazanej
                                if (IsArrayInList(wrongPath, result) == false)
                                {
                                    shortestPath = matrix[currentNode, j];
                                    closestNode = j;
                                }
                                result[i + 1] = -1;
                            }
                        }
                    }
                    //nie dalo sie wygenerowac sciezki, proponowana droga trafia na liste zakazana
                    if (closestNode == -1)
                    {
                        // brak rozwiazania dla tego grafu
                        if (IsArrayInList(wrongPath, result) == true)
                        {
                            isWrongPath = false;
                            bestResult = INF;
                            break;
                        }
                        else
                        {
                            wrongPath.Add(result);
                            //mozna przerwac
                            i = nodesConut;
                        }
                    }
                    else
                    {
                        bestResult += matrix[currentNode, closestNode];
                        currentNode = closestNode;
                        result[i + 1] = currentNode;
                        closestNode = -1;
                        Console.Write(currentNode);
                    }
                }
            }
        } while (isWrongPath == true);

        for (int i = 0; i < nodesConut; i++)
        {
            bestPath[i] = result[i];
        }
    }

    /// <summary>
    /// sprawdza czy w liscie zakazanej istnieje juz badana sciezka
    /// </summary>
    /// <param name="listOfArrays">lista drog zakazanych</param>
    /// <param name="array">badania droga</param>
    /// <returns>true jesli podana droga istnieje juz na liscie</returns>
    private bool IsArrayInList(List<int[]> listOfArrays, int[] array)
    {
        if (listOfArrays.Count == 0)
        {
            return false;
        }
        else
        {
            bool isCopy = false;
            for (int i = 0; i < listOfArrays.Count; i++)
            {
                isCopy = false;
                int[] tempArray = listOfArrays[i];
                for (int j = 0; j < array.Count(); j++)
                {
                    if (tempArray[j] != array[j])
                    {
                        isCopy = false;
                        //przerwij to sprawdzanie bo ciagi sa rozne
                        j = array.Count();
                    }
                    else
                    {
                        isCopy = true;
                    }
                }
                if (isCopy == true)
                {
                    return isCopy;
                }
            }
            return isCopy;
        }
    }
}
