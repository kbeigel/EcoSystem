import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Ecosystem extends PApplet {

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
public void setup()
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
  //fullScreen();
    // Specify P3D renderer
  background(153);
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
public void draw()
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

public void keyPressed() {

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
class Algae 
{ 
  ArrayList<PVector> circles;
  float minRad=0, maxRad= 1;
  float distanceToCenter;
  float newR; 
  float newX; 
  float newY;
  float margin;
  int growthTimer;
  

  Algae() {
    float initialX = random(0, width);
    float initialY = random(0, height);

    initialize();

    circles = new ArrayList<PVector>();
    circles.add(new PVector (initialX, initialY, random(minRad, maxRad)));
  }

  public void initialize()
  {
    colorMode(RGB); 
    margin = 50;
    growthTimer = 0;
  }

  public void Update()
  {
    fill(0, 130, 10); //base color
    
    if (growthTimer >= 4 && circles.size() > 0) 
    {
      noStroke();
      smooth();
      newR = random(minRad, maxRad);
      newX = random(-margin + newR, margin + width-newR);
      newY = random(-margin + newR, margin + height-newR);
      distanceToCenter = dist (newX, newY, circles.get(0).x, circles.get(0).y);
     

      if (distanceToCenter < 300) {

        float closestDist = 100000000;
        int closestIndex = 0;
        float distance;

        // which circle is the closest?
        for (int i=0; i < circles.size(); i++)
        {
          distance = dist(newX, newY, circles.get(i).x, circles.get(i).y);
          if (distance < closestDist) {
            closestDist = distance;
            closestIndex = i;
          }
        }
       

        // align it to the closest circle outline
        float angle = atan2(newY-circles.get(closestIndex).y, newX-circles.get(closestIndex).x);
        float deltaX = cos(angle) *circles.get(closestIndex).z;
        float deltaY = sin(angle) * circles.get(closestIndex).z;

        // draw it
        fill(55, 170, 30); //flash color
        newR =  exp(map (closestDist, 0, width, 1, 7));
        newX = circles.get(closestIndex).x  + deltaX;
        newY =  circles.get(closestIndex).y + deltaY;
        circles.add(new PVector (newX, newY, newR));
      }

        
        growthTimer = 0;         
    }
      for (int i = 0; i < circles.size(); i++)
      ellipse(circles.get(i).x, circles.get(i).y, circles.get(i).z*2, circles.get(i).z*2 );
      growthTimer++;
  }
}
class Consumer { //<>//

  PVector location;
  PShape image;
  int tintHue;
  int drawWidth;
  int drawHeight;
  int margin;
  int moveRange;
  PVector velocity;
  float maxforce;    // Maximum steering force
  float maxspeed;    // Maximum speed
  float eatRange;
  int framesSinceFood;
  int foodLagTimer;
  int mealTimer = 0;
  int eatStage = 0;
  int predatorEatRange = 50;
  int friendlyEatRange = 300;
  Predator target;
  boolean hasTarget = false;

  //this is the predator constructor
  Consumer() {
    Initialize();
  }

  //initialize method
  public void Initialize() {
    location   = new PVector(random(-margin, width + margin), random(-margin, height + margin));
    tintHue    = PApplet.parseInt(random(215, 235));
    image      = loadShape("consumer.svg");
    drawWidth  = 7;
    drawHeight = 7;
    margin = 7;
    moveRange  = 3;
    velocity = new PVector(5, 0);
    maxforce = 1.0f;
    maxspeed = 10.0f; 
    eatRange = 6.0f;
    framesSinceFood = 0;
    foodLagTimer = 0;
  }

  public void Update(ArrayList<Algae> algaes, ArrayList<Consumer> consumers) 
  {
    //with every update, the consumer will move, seek target algae, eat target algae within range and it will draw itself
    if (eatStage != 0 && eatStage != 2 && !hasTarget)
      for (Predator p : ListOfPredators)
      {
        float distance = PVector.dist(p.location, this.location);
        if (distance < predatorEatRange)
        {
          this.chasePredator(p);
          this.recruitClosestConsumers(consumers);
        }
      }

    if (!ListOfPredators.contains(target))
    {
      hasTarget = false;
    }

    render();
    move(algaes);
    wrapAround();
    eat(algaes);

    framesSinceFood++;
  }

  //drawing the consumer
  public void render() {
    // Draw a consumer rotated in the direction of velocity


    float theta = velocity.heading() + radians(90);
    colorMode(RGB);
    fill(200, 60, 17);
    shape(image, location.x, location.y, drawWidth, drawHeight);
    pushMatrix();
    rotate(theta);
    popMatrix();
  }

  //updating velocity and location
  public void move(ArrayList<Algae> algaes) {

    switch(eatStage)
    {
      //when eatStage = 0, seeking movement type
    case 0: 
      {
        // Update velocity
        if (algaes.size() > 0)
          velocity.add(getAcceleration(findClosestCircle(findClosestAlgae(algaes))));
        // Limit speed
        velocity.limit(maxspeed);
        location.add(velocity);
        break;
      }

      //when eatStage = 1, staying in place when feeding
    case 1: 
      {
        velocity = new PVector();
        location.add(velocity);
        break;
      }     
      //when eatStage = 2, brownian movement
    case 2: 
      {
        location.x += random(-moveRange, moveRange);
        location.y += random(-moveRange, moveRange);
        break;
      }
    case 3:
      {
        velocity.add(getAcceleration(target.location));
        velocity.limit(maxspeed);
        location.add(velocity);
        break;
      }
    }
  }

  //keeping everything on the screen by having it wrap around
  public void wrapAround() {
    if (location.x < -margin) location.x = width+margin;
    if (location.y < -margin) location.y = height+margin;
    if (location.x > width+margin) location.x = -margin;
    if (location.y > height+margin) location.y = -margin;
  }

  public void eat(ArrayList<Algae> algaes)
  {
    if (algaes.size() > 0 || ListOfPredators.size() > 0)
    {
      PVector closestCircle = findClosestCircle(findClosestAlgae(algaes));

      switch(eatStage)
      {
        //seeking a new circle
      case 0:
        {
          if(findClosestAlgae(algaes) == null)
          {
            eatStage = 2;
          }
          else if (PVector.dist(location, closestCircle) < eatRange)
          {
            eatStage++;
            foodLagTimer = 0;
          }
          break;
        }

        //eating
      case 1:
        {
          if (findClosestAlgae(algaes) != null && mealTimer > 50)
          {
            findClosestAlgae(algaes).circles.remove(closestCircle);
            framesSinceFood = 0;
            eatStage++;
          }
          mealTimer++;
          break;
        }
        //lagging
      case 2:
        {
          if (foodLagTimer > 70)
          {
            eatStage = 0;
            mealTimer = 0;
          }
          foodLagTimer++;

          break;
        }
      case 3:
        {
          float distance = PVector.dist(this.location, target.location);
          if (distance < eatRange)
          {
            ListOfPredators.remove(target);
            framesSinceFood = 0;
            eatStage--;
          }
          break;
        }
      }
    }
  }

  //determines if the predator has had enough food to survive
  public boolean dying()
  {
    //number of frames that it takes for the prey to die
    if (framesSinceFood > 600)
    {
      return true;
    }
    return false;
  }

  //finds the closest prey  
  public Algae findClosestAlgae(ArrayList<Algae> algaes) {

    Algae closestAlgae = null;

    float closestDistance = 999999;

    //for each prey (named algae a) in the list preys, do this loop
    for (Algae a : algaes) {
      //check if p is inside of the range of the consumer
      //compare the positions
      //distance can be called without instance
      //compare the location of the consumer to the locaiton of the algae
      if (a.circles.size() > 0)
      {
        float distance = PVector.dist(location, (a.circles.get(0)));
        if (distance < closestDistance) {
          closestDistance = distance;
          closestAlgae = a;
        }
      }
    }

    return closestAlgae;
  }

  //finds the closest circle on a given algae
  public PVector findClosestCircle(Algae algae)
  {
    PVector closestCircle = new PVector();
    if (algae != null && algae.circles.size() > 0)
    {
      closestCircle = algae.circles.get(0);  

      float closestDistance = 999999; 

      for (int i = 0; i < algae.circles.size(); i++)
      {
        float distance = PVector.dist(location, algae.circles.get(i));
        if (distance < closestDistance) {
          closestDistance = distance;
          closestCircle = algae.circles.get(i);
        }
      }
    }

    return closestCircle;
  }

  public PVector getAcceleration(PVector destination) {
    // A vector pointing from the location to the target
    PVector vectorTowardTarget = PVector.sub(destination, location);  
    // Scale to maximum speed
    vectorTowardTarget.normalize();
    vectorTowardTarget.mult(maxspeed);

    // Steering = Desired minus Velocity
    PVector acceleration = PVector.sub(vectorTowardTarget, velocity);
    acceleration.limit(maxforce);  // Limit to maximum steering force
    return acceleration;
  }

  public void chasePredator(Predator t)
  {
    this.target = t;
    eatStage = 3;
    hasTarget = true;
  }

  // gets the closest consumers
  public void recruitClosestConsumers(ArrayList<Consumer> consumers)
  {
    for (Consumer c : consumers)
    {
      ArrayList<Consumer> followingConsumers = new ArrayList<Consumer>();
      int numFollowing = 0;
      // thing.method
      // contains returns a boolean value
      if (numFollowing < 4 && !followingConsumers.contains(c))
      {
        // c is every consumer in the list; this will be done for every consumer in the list)
        float distance = PVector.dist(c.location, this.location);

        if (distance < friendlyEatRange)
        {
          followingConsumers.add(c);
          c.chasePredator(this.target);
          numFollowing++;
        }
      }
    }
  }
}
// The Flock (a list of Prey objects)

class Flock {
    ArrayList<Prey> preys; // An ArrayList for all the preys

    Flock() {
        // Initialize the ArrayList
        preys = new ArrayList<Prey>();
        // FLOCK SETUP
        for (int i = 0; i < initialPreys; i++) 
        {
            addPrey(new Prey(width/2, height/2));
        }
    }
    //Update method happens every time it is called in the draw method in Ecosystem
    public void Update(ArrayList<Plankton> planktons) {
        ArrayList<Prey> dying = new ArrayList<Prey>();
        if (preys.size() > 0) {
            for (Prey prey : preys) {
                // Passing the entire list of preys to each prey individually
                prey.Update(preys, planktons); 
                //boolean, "if prey is dying"
                if (prey.dying())
                {
                    // prey is added to the dying array list
                    dying.add(prey);
                }
            }

            for (Prey prey : dying)
            {
                preys.remove(prey);
            }
        }
    }

    public void addPrey(Prey b) {
        preys.add(b);
    }

    public int getSize() {
        return preys.size();
    }
}
//all features are public in the class
class Plankton
{
  PVector location;
  PShape image;
  int tintHue;
  int drawWidth;
  int drawHeight;
  int moveRange;

  //sets a random location
  Plankton()
  {
    initialize();

    location.x = random(0, width);
    location.y = random(0, height);
  }

  //gives the plankton a specific location
  Plankton(float x, float y)
  {
    initialize();

    location.x = x;
    location.y = y;
  }

  //sets the initial values
  private void initialize()
  {
    location   = new PVector();
    tintHue    = PApplet.parseInt(random(0, 250));
    image      = loadShape("creaturesart2500.svg");
    drawWidth  = 5;
    drawHeight = 5;
    moveRange  = 3;
  }

  private void Draw()
  {
    colorMode(RGB);  
    noStroke();
    fill(55, 190, 140);
    ellipse(location.x, location.y, drawWidth, drawHeight);

    /* keeping these incase i want to change to an image later
     tint(tintHue);
     shape(image, location.x, location.y, drawWidth, drawHeight);
     pushMatrix();
     rotate(90);
     popMatrix();
     */
  }

  //adding a random move range value into the location
  private void moveRandomly()
  { 
    location.x += random(-moveRange, moveRange);
    location.y += random(-moveRange, moveRange);
  }

  public void Update()
  {
    Draw();
    moveRandomly();
    //if timer desired, will be added here-- changes the frequency of movement
  }

  public PVector getLocation()
  {
    return location;
  }

  public float getx()
  {
    return location.x;
  }

  public float gety()
  {
    return location.y;
  }
}
class Predator {

  PVector location;
  PShape image;
  int tintHue;
  int drawWidth;
  int drawHeight;
  int margin;
  int moveRange;
  PVector velocity;
  // Maximum steering force
  float maxforce;
  // Maximum speed
  float maxspeed;    
  float eatRange;
  int framesSinceFood;
  int foodLagTimer;
  boolean isFull;

  // this is the predator constructor
  Predator() {
    Initialize();
  }

  // initialize method
  public void Initialize() {
    location   = new PVector(random(-margin, width + margin), random(-margin, height + margin));
    tintHue    = PApplet.parseInt(random(215, 235));
    image      = loadShape("creaturesart2500.svg");
    drawWidth  = 6;
    drawHeight = 6;
    margin = 7;
    moveRange  = 9;
    velocity = new PVector(5, 0);
    maxforce = 1.0f;
    maxspeed = 7.0f; 
    eatRange = 6.0f;
    framesSinceFood = 0;
    foodLagTimer = 0;
    isFull = false;
  }

  public void Update(ArrayList<Prey> preys) {
    //with every update, the predator will move, seek target prey, eat target prey within range and it will draw itself
    render();
    move(preys);
    wrapAround();
    eat(preys);

    framesSinceFood++;
    if (isFull)
      foodLagTimer++;

    if (foodLagTimer > 100)
    {
      isFull = false;
      foodLagTimer = 0;
    }
  }

  // drawing the predator
  public void render() {
    // Draw a predator rotated in the direction of velocity

    colorMode(RGB);  
    noStroke();
    fill(255, 10, 10);
    ellipse(location.x, location.y, drawWidth, drawHeight);

    /*float theta = velocity.heading() + radians(90);
            /*colorMode(RGB);
     fill(200, 60, 17);
     shape(image, location.x, location.y, drawWidth, drawHeight);
     pushMatrix();
     rotate(theta);
     popMatrix();*/
  }

  // updating velocity and location
  public void move(ArrayList<Prey> preys) {
    // Update velocity
    if (preys.size() > 0 && !isFull)
      velocity.add(getAcceleration(findClosestPrey(preys).location));
    // Limit speed
    velocity.limit(maxspeed);
    location.add(velocity);
  }

  // keeping everything on the screen by having it wrap around
  public void wrapAround() {
    if (location.x < -margin) location.x = width+margin;
    if (location.y < -margin) location.y = height+margin;
    if (location.x > width+margin) location.x = -margin;
    if (location.y > height+margin) location.y = -margin;
  }

  public void eat(ArrayList<Prey> preys)
  {
    if (preys.size() > 0 && !isFull)
    {
      Prey closestPrey = findClosestPrey(preys);

      if (PVector.dist(location, closestPrey.location) < eatRange)
      {
        preys.remove(closestPrey);
        framesSinceFood = 0;
        isFull = true;
      }
    }
  }

  //  determines if the predator has had enough food to survive
  public boolean dying()
  {
    // number of frames that it takes for the prey to die
    if (framesSinceFood > 1000)
    {
      return true;
    }
    return false;
  }

  //finds the closest prey  
  public Prey findClosestPrey(ArrayList<Prey> preys) {

    Prey closestPrey = null;

    float closestDistance = 999999;

    // for each prey (named prey p) in the list preys, do this loop
    for (Prey p : preys) {
      // check if p is inside of the range of the predator
      // compare the positions
      // distance can be called without instance
      // compare the location of the predator to the locaiton of the prey
      float distance = PVector.dist(location, p.location);
      if (distance < closestDistance) {
        closestDistance = distance;
        closestPrey = p;
      }
    }

    return closestPrey;
  }

  public PVector getAcceleration(PVector target) {
    // A vector pointing from the location to the target
    PVector vectorTowardTarget = PVector.sub(target, location);  
    // Scale to maximum speed
    vectorTowardTarget.normalize();
    vectorTowardTarget.mult(maxspeed);

    // Steering = Desired minus Velocity
    PVector acceleration = PVector.sub(vectorTowardTarget, velocity);
    // Limit to maximum steering force
    acceleration.limit(maxforce);  
    return acceleration;
  }
}
class Prey {

  PVector location;
  PVector velocity;
  PVector acceleration;
  float margin;
  float maxforce;
  float maxspeed;
  float eatRange;
  int framesSinceFood;
  PShape img;
  float drawWidth;
  float drawHeight;
  boolean isFull;
  int foodLagTimer;

  Prey()
  {
    initialize();

    location.x = random(-margin, width + margin);
    location.y = random(-margin, height + margin);
  }

  Prey(float x, float y)
  {
    initialize();

    location.x = x;
    location.y = y;
  }

  // initialize flock
  private void initialize()
  {
    img = loadShape("creaturesart2500-1.svg");

    acceleration = new PVector(0, 0);

    // sets the initial velocity of the prey
    float angle = random(TWO_PI);
    velocity = new PVector(cos(angle), sin(angle));

    location = new PVector();
    margin = 7.0f;
    maxspeed = 6;
    maxforce = 0.09f;
    eatRange = 5.0f;
    drawWidth  = 10;
    drawHeight = 15;
    isFull = false;
    framesSinceFood = 0;
    foodLagTimer = 0;
    
  }

  // devours the closest plankton if its in range
  public void eat(ArrayList<Plankton> planktons)
  {
    Plankton closestPlankton = findClosestPlankton(planktons);

    if (!isFull && PVector.dist(location, closestPlankton.location) < eatRange)
    {
      planktons.remove(closestPlankton); //eating
      framesSinceFood= 0;
      isFull = true;
    }
    if (foodLagTimer > 900)
    {
      isFull = false;
      foodLagTimer = 0;
    }
    foodLagTimer++;
  }

  // finds the closest plankton
  private Plankton findClosestPlankton(ArrayList<Plankton> planktons)
  {        
    Plankton closestPlankton = planktons.get(0);

    float closestDistance = 9999999;

    for (Plankton plankton : planktons)
    {
      float distance = PVector.dist(location, plankton.getLocation());
      if (distance < closestDistance)
      {
        closestDistance = distance;
        closestPlankton = plankton;
      }
    }

    return closestPlankton;
  }

  // determines if the prey has had enough food to survive
  public boolean dying()
  {
    // number of frames that it takes for the prey to die
    if (framesSinceFood > 900)
    {
      return true;
    }
    return false;
  }

  // moves, draws, and feeds the prey
  public void Update(ArrayList<Prey> preys, ArrayList<Plankton> planktons) {
    flock(preys);
    move();
    borders();
    render();
    eat(planktons);

    framesSinceFood++;
  }

  public void applyForce(PVector force) {
    acceleration.add(force);
  }

  // Sets acceleration
  // We accumulate a new acceleration each time based on three rules
  public void flock(ArrayList<Prey> preys) {
    // Seperation
    PVector sep = separate(preys);
    // Alignment
    PVector ali = align(preys);
    // Cohesion
    PVector coh = cohesion(preys);
    // Arbitrarily weight these forces
    sep.mult(2.5f);
    ali.mult(3);
    coh.mult(1);
    // Add the force vectors to acceleration
    applyForce(sep);
    applyForce(ali);
    applyForce(coh);
  }

  // Method to update location
  public void move() {
    // Update velocity
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    location.add(velocity);
    // Reset accelertion to 0 each cycle
    acceleration.mult(0);
  }

  // A method that calculates and applies a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  public PVector seek(PVector target) {
    // A vector pointing from the location to the target
    PVector desired = PVector.sub(target, location);  
    // Scale to maximum speed
    desired.normalize();
    desired.mult(maxspeed);

    // Steering = Desired minus Velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    return steer;
  }

  public void render() {
    // Draw an organism rotated in the direction of velocity
    float theta = velocity.heading() + radians(90);
    /*
           colorMode(RGB);  
     noStroke();
     fill(40, 30, 190);
     triangle((location.x), (location.y+(drawHeight/2)), (location.x-(drawWidth/2)), (location.y-(drawHeight/2)), (location.x+(drawWidth/2)), (location.y-(drawHeight/2)));
     rotate(1);
     */


    shape(img, location.x, location.y, 10, 10);
    pushMatrix();
    rotate(theta);
    popMatrix();
  }

  // Wraparound
  public void borders() {
    if (location.x < -margin) location.x = width+margin;
    if (location.y < -margin) location.y = height+margin;
    if (location.x > width+margin) location.x = -margin;
    if (location.y > height+margin) location.y = -margin;
  }

  ///////////////////////////////////////////////////////////////////////////////////////// DONT CARE
  /////// this has not been altered from the generic flocking code from processing.org

  // Separation
  // Method checks for nearby preys and steers away
  public PVector separate (ArrayList<Prey> preys) {
    float desiredseparation = 25.0f;
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    // For every prey in the system, check if it's too close
    for (Prey other : preys) {
      float d = PVector.dist(location, other.location);
      // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
      if ((d > 0) && (d < desiredseparation)) {
        // Calculate vector pointing away from neighbor
        PVector diff = PVector.sub(location, other.location);
        diff.normalize();
        diff.div(d);        // Weight by distance
        steer.add(diff);
        count++;            // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.div((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.mag() > 0) {
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // steer.setMag(maxspeed);

      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize();
      steer.mult(maxspeed);
      steer.sub(velocity);
      steer.limit(maxforce);
    }
    return steer;
  }

  // Alignment
  // For every nearby prey in the system, calculate the average velocity
  public PVector align (ArrayList<Prey> preys) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Prey other : preys) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.velocity);
        count++;
      }
    }
    if (count > 0) {
      sum.div((float)count);
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // sum.setMag(maxspeed);

      // Implement Reynolds: Steering = Desired - Velocity
      sum.normalize();
      sum.mult(maxspeed);
      PVector steer = PVector.sub(sum, velocity);
      steer.limit(maxforce);
      return steer;
    } else {
      return new PVector(0, 0);
    }
  }

  // Cohesion
  // For the average location (i.e. center) of all nearby preys, calculate steering vector towards that location
  public PVector cohesion (ArrayList<Prey> preys) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);
    // Start with empty vector to accumulate all locations
    int count = 0;
    for (Prey other : preys) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        // Add location
        sum.add(other.location); 
        count++;
      }
    }
    if (count > 0) {
      sum.div(count);
      // Steer towards the location
      return seek(sum);
    } else {
      return new PVector(0, 0);
    }
  }
}
  public void settings() {  size(1600, 800, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Ecosystem" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
