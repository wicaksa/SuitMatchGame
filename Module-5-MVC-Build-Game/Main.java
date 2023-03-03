
public class Main
{
   public static void main(String[] args)
   {
      Model gameModel = new Model(1, 0, 0, null, 2, 7);
      View gameView = new View("Build", 7, 2);
      Controller gameControl = new Controller(gameModel, gameView);
   }
}