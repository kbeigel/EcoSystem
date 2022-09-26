//fonts being declared here, equal to null-- not said what they are; later these will be made and everyhting should be able to access them
PFont font;
ArrayList<Plankton> planktons;
int initialPlanktons = 200;
int initialPreys = 150;
int initialPredators = 30;
int initialAlgaes =15;
int initialConsumers = 25;
static ArrayList<Predator> ListOfPredators;
ArrayList<Algae> algaes;
ArrayList<Consumer> consumers;
Flock flock;
Prey prey;

//acts as the main
//when the main is called, main calls setup
//this is the very first thing that occurs
void setup()
{
  //font is being created here
  font        = createFont("Arial", 12);
  // creating a new list
  planktons = new ArrayList<Plankton>();
  // constructor for Flock
  flock  = new Flock();
  //constructor for predators
  ListOfPredators = new ArrayList<Predator>();
  // constructor for algaes
  algaes = new ArrayList<Algae>();
  //constructor for consumers
  consumers = new ArrayList<Consumer>();

  //screen size
  fullScreen();
  //frameRate(30);

  //ALGAE SETUP
  for (int i = 0; i < initialAlgaes; i++)
  {
    algaes.add(new Algae());
  }

  // PLANKTON SETUP
  for (int i = 0; i < initialPlanktons; i++)
  { 
    planktons.add(new Plankton());
  }

  // PREDATOR SETUP
  for (int i = 0; i <initialPredators; i++)
  {
    ListOfPredators.add(new Predator());
  }

  // CONSUMER SETUP
  for (int i = 0; i < initialConsumers; i++)
  {
    consumers.add(new Consumer());
  }
}

//draw occurs every time that the program runs through
// this will be the updates for all of the organisms-- how the code updates every time it runs through
void draw()
{
  // background color
  background(20);

  //ALGAE UPDATE
  for (Algae algae : algaes)
  {
    algae.Update();
  }

  // PLANKTON UPDATE
  if (planktons.size() > 0)
  {
    for (Plankton plankton : planktons)
    {
      plankton.Update();
    }
  }

  // FLOCK UPDATE 
  flock.Update(planktons);

  // PREDATOR UPDATE
  ArrayList<Predator> dyingPredators = new ArrayList<Predator>();
  if (ListOfPredators.size() > 0)
  {
    for (Predator predator : ListOfPredators)
    {
      predator.Update(flock.preys);

      if (predator.dying())
        dyingPredators.add(predator);
    }

    for (Predator p : dyingPredators)
    {
      ListOfPredators.remove(p);
    }
  }
  // CONSUMER UPDATE
    ArrayList<Consumer> dyingConsumers = new ArrayList<Consumer>();
    if (consumers.size() > 0)
    {
      for (Consumer consumer : consumers)
      {
        consumer.Update(algaes, consumers);

        if (consumer.dying())
          dyingConsumers.add(consumer);
      }

      for (Consumer c : dyingConsumers)
      {
        consumers.remove(c);
      }
    }

  // text color
  /*fill(200);
   text("Algae:  " +algaes.size()+ " -   Press 'a' to generate Algae", 30, height-130);
   text("Plankton: " +planktons.size()+   " -   Press 'k' to generate Plankton", 30, height-100);
   text("Prey:  " +flock.getSize()+ " -   Press 'f' to generate Prey", 30, height-70);
   text("Consumer:  " +consumers.size()+ " -   Press 'c' to generate Consumer", 30, height-40);
   text("Predator:  " +predators.size()+ " -   Press 'p' to generate Predator", 30, height-10);
   */
}

void keyPressed() {

  if (key=='d') { 
    algaes.add(new Algae());
  }
  if (key=='h') { 
    consumers.add(new Consumer());
  }
  if (key=='f') { 
    planktons.add(new Plankton());
  }
  if (key=='g') {
    flock.addPrey(new Prey());
  }

  if (key=='j') {
    ListOfPredators.add(new Predator());
  }
}
