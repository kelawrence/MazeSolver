package mazesolver;

import java.io.*;

/**
 * Creates a maze from a text file, attempts to solve the maze and outputs the
 * resolution if there is one.
 *
 * @author Krysten Lawrence
 */
public class MazeSolver
{

    private char[][] maze = null;
    private BufferedReader myFile = null;
    private File fileName;
    private static int startCol = 0;
    private static int startRow = 0;
    private int endCol = 0;
    private int endRow = 0;
    private static int rows = 0;
    private static int cols = 0;

    public static void main(String[] args)
    {
        MazeSolver solved = new MazeSolver();
        String fileName = "large_input.txt";
        solved.readMaze(fileName);
        solved.buildMaze();
        if (solved.solveMaze(startRow, startCol))
        {
            solved.printMaze();
        }
        else
        {
            System.out.println("This maze is unsolvable.");
        }
    }

    /**
     * Generates the maze from the specified maze file and stores the values for
     * the output maze file.
     *
     * @param afile The name of the file to be read
     */
    private void readMaze(String afile)
    {
        char temp;
        String fileLine = null;
        int pointer = 1;
        int rowPointer = 0;

        try
        {
            fileName = new File(afile);
            myFile = new BufferedReader(new FileReader(afile));
            while ((fileLine = myFile.readLine()) != null)
            {
                switch (pointer)
                {
                    case (1):
                        //Get the integers of the columns and rows for the maze.
                        cols = Integer.parseInt(fileLine.substring(0,
                                fileLine.indexOf(' ')));
                        rows = Integer.parseInt((fileLine.substring(
                                fileLine.indexOf(' ') + 1)));
                        maze = new char[rows][cols];
                        break;
                    case (2):
                        //Get the starting position for the maze.
                        temp = fileLine.charAt(0);
                        startCol = Character.getNumericValue(temp);
                        temp = fileLine.charAt(2);
                        startRow = Character.getNumericValue(temp);
                        break;
                    case (3):
                        //Get the finishing position for the maze.
                        endCol = Integer.parseInt(fileLine.substring(0,
                                fileLine.indexOf(' ')));
                        endRow = Integer.parseInt((fileLine.substring(
                                fileLine.indexOf(' ') + 1)));
                        break;
                    default:
                        //Get the integers that represent the maze to be solved.
                        int colPointer = 0;
                        for (int i = 0; i < fileLine.length(); i++)
                        {
                            if (fileLine.charAt(i) != ' ')
                            {
                                maze[rowPointer][colPointer] = fileLine.charAt(i);
                                colPointer++;
                            }
                        }
                        rowPointer++;
                        break;
                }
                pointer++;
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Unable to find file " + fileName + ".");
            System.exit(1);
        }
        catch (IOException e)
        {
            System.out.println("There was an error writing the solution");
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
    }

    private void buildMaze()
    {
        //Convert the values in the input maze to display 
        //the values in the maze to be displayed
        maze[startRow][startCol] = 'S';
        maze[endRow][endCol] = 'E';
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (maze[i][j] == '1')
                {
                    maze[i][j] = '#';
                }
                if (maze[i][j] == '0')
                {
                    maze[i][j] = ' ';
                }
            }
        }
    }

    /**
     * Finds the path from start to finish, marking each point that has been
     * visited after using a flood fill to process the cells.
     */
    private boolean solveMaze(int aRow, int aCol)
    {
        //Array to hold the length of the path from the start
        int[][] path = new int[rows][cols];
        //Fill in the array with -2 to indicate the unprocessed cells
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                path[i][j] = -2;
            }
        }
        //Initialise the list of cells to be processed during the flood fill
        int[][] toDo = new int[rows * cols][2];
        toDo[0][0] = startRow;
        toDo[0][1] = startCol;
        path[startRow][startCol] = 0;
        int processNext = 0, endOfList = 1;
        while (processNext != endOfList && path[endRow][endCol] == -2)
        {
            //Store current row and column
            int cRow = toDo[processNext][0];
            int cCol = toDo[processNext][1];
            //Store current row and column number of path cell
            int cPathLength = path[cRow][cCol];
            //Process N if it is within the maze
            if (cRow > 0)
            {
                //Check to see if the next step of the path can be to the north
                if (path[cRow - 1][cCol] == -2)
                {
                    //Check the cell to the north is not a wall
                    if (maze[cRow - 1][cCol] != '#')
                    {
                        //Wrap the path around if it is at the edge of the maze
                        if (maze[0][cCol] != '#')
                        {
                            cRow = rows - 1;
                        }
                        //Record the current length of path and add the cell
                        //to the list of cells to be marked
                        path[cRow - 1][cCol] = cPathLength + 1;
                        toDo[endOfList][0] = cRow - 1;
                        toDo[endOfList][1] = cCol;
                        endOfList++;
                    }
                    else
                    {
                        //This cell is a wall, will no longer be processed
                        path[cRow - 1][cCol] = -1;
                    }
                }
            }
            //Check to see if the next step of the path can be to the south
            if (cRow < rows - 1)
            {
                //Check to see if the cell to the south has been processed
                if (path[cRow + 1][cCol] == -2)
                {
                    //Check the cell to the south is not a wall
                    if (maze[cRow + 1][cCol] != '#')
                    {
                        //Wrap the path around if it is at the edge of the maze
                        if (maze[rows - 1][cCol] != '#')
                        {
                            cRow = -1;
                        }
                        //Record the current length of path and add the cell
                        //to the list of cells to be marked
                        path[cRow + 1][cCol] = cPathLength + 1;
                        toDo[endOfList][0] = cRow + 1;
                        toDo[endOfList][1] = cCol;
                        endOfList++;
                    }
                    else
                    {
                        //This cell is a wall, will no longer be processed
                        path[cRow + 1][cCol] = -1;
                    }
                }
            }
            //Check to see if the next step of the path can be to the east
            if (cCol < cols - 1)
            {
                //Check to see if the cell to the east has been processed
                if (path[cRow][cCol + 1] == -2)
                {
                    //Check the cell to the east is not a wall
                    if (maze[cRow][cCol + 1] != '#')
                    {
                        if (maze[cRow][cols - 1] != '#')
                        {
                            cCol = -1;
                        }
                        //Record the current length of path and add the cell
                        //to the list of cells to be marked
                        path[cRow][cCol + 1] = cPathLength + 1;
                        toDo[endOfList][0] = cRow;
                        toDo[endOfList][1] = cCol + 1;
                        endOfList++;
                    }
                    else
                    {
                        //This cell is a wall, will no longer be processed
                        path[cRow][cCol + 1] = -1;
                    }
                }
            }
            //Check to see if the next step of the path can be to the west
            if (cCol > 0)
            {
                //Check to see if the cell to the west has been processed
                if (path[cRow][cCol - 1] == -2)
                {
                    //Check the cell to the west is not a wall
                    if (maze[cRow][cCol - 1] != '#')
                    {
                        //Wrap the path around if it is at the edge of the maze
                        if (maze[cRow][0] != '#')
                        {
                            cCol = cols - 1;
                        }
                        //Record the current length of path and add the cell
                        //to the list of cells to be marked
                        path[cRow][cCol - 1] = cPathLength + 1;
                        toDo[endOfList][0] = cRow;
                        toDo[endOfList][1] = cCol - 1;
                        endOfList++;
                    }
                    else
                    {
                        //This cell is a wall, will no longer be processed
                        path[cRow][cCol - 1] = -1;
                    }
                }
            }
            //We have finished processing the cells around this one, 
            //so move onto next
            processNext++;
        }
        //Backtrack along the path filling in the steps taken with 'X'
        int pathRow = endRow;
        int pathCol = endCol;
        int currentPathLength;
        //We know what path to take and how long it is, so a for loop will work
        //There is some issue here with getting the maze to wrap properly
        //I know I am probably missing something here as the maze is processed
        //as being solvable, but when backfilling, it will mark from the end
        //point to where the maze wraps but then doesn not fill to the start
        for (int i = 1; i <= path[endRow][endCol]; i++)
        {
            currentPathLength = path[pathRow][pathCol];
            //Follow the path north
            if (path[pathRow - 1][pathCol] == currentPathLength - 1)
            {
                //If at edge of maze, wrap path and mark cells
                if (pathRow - 1 == 0)
                {
                    maze[pathRow][pathCol] = 'X';
                    pathRow = rows - 1;
                    maze[pathRow][pathCol] = 'X';
                }
                maze[pathRow - 1][pathCol] = 'X';
                pathRow--;
            }
            //If not north, follow path south
            else if (path[pathRow + 1][pathCol] == currentPathLength - 1)
            {
                //If at edge of maze, wrap path and mark cells
                if (pathRow + 1 == rows - 1)
                {
                    maze[pathRow + 1][pathCol] = 'X';
                    pathRow = 0;
                    maze[pathRow][pathCol] = 'X';
                }
                maze[pathRow + 1][pathCol] = 'X';
                pathRow++;
            }
            //If not north or south, follow path east
            else if (path[pathRow][pathCol + 1] == currentPathLength - 1)
            {
                //If at edge of maze, wrap path and mark cells
                if (pathCol + 1 == cols - 1)
                {
                    maze[pathRow][pathCol + 1] = 'X';
                    pathCol = 0;
                    maze[pathRow][pathCol] = 'X';
                }
                maze[pathRow][pathCol + 1] = 'X';
                pathCol++;
            }
            //If not north, south or east, follow path west
            else if (path[pathRow][pathCol - 1] == currentPathLength - 1)
            {
                //If at edge of maze, wrap path and mark cells
                if (pathCol - 1 == 0)
                {
                    maze[pathRow][0] = 'X';
                    pathCol = cols - 1;
                    maze[pathRow][pathCol] = 'X';
                }
                maze[pathRow][pathCol - 1] = 'X';
                pathCol--;
            }
        }
        if (path[endRow][endCol] != -2)
        {
            // Returns true if the end point has been processed
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Prints the solved maze to the console
     */
    private void printMaze()
    {
        maze[startCol][startRow] = 'S';
        for (int i = 0; i < maze.length; i++)
        {
            System.out.println(maze[i]);
        }
    }
}
