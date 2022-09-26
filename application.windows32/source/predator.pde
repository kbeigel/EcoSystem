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
  void Initialize() {
    location   = new PVector(random(-margin, width + margin), random(-margin, height + margin));
    tintHue    = int(random(215, 235));
    image      = loadShape("creaturesart2500.svg");
    drawWidth  = 6;
    drawHeight = 6;
    margin = 7;
    moveRange  = 9;
    velocity = new PVector(5, 0);
    maxforce = 1.0;
    maxspeed = 7.0; 
    eatRange = 6.0;
    framesSinceFood = 0;
    foodLagTimer = 0;
    isFull = false;
  }

  void Update(ArrayList<Prey> preys) {
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
  void render() {
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
  void move(ArrayList<Prey> preys) {
    // Update velocity
    if (preys.size() > 0 && !isFull)
      velocity.add(getAcceleration(findClosestPrey(preys).location));
    // Limit speed
    velocity.limit(maxspeed);
    location.add(velocity);
  }

  // keeping everything on the screen by having it wrap around
  void wrapAround() {
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
  boolean dying()
  {
    // number of frames that it takes for the prey to die
    if (framesSinceFood > 1000)
    {
      return true;
    }
    return false;
  }

  //finds the closest prey  
  Prey findClosestPrey(ArrayList<Prey> preys) {

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

  PVector getAcceleration(PVector target) {
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
