class Prey {

  PVector location;
  PVector velocity;
  int tintHue;
  PVector acceleration;
  float margin;
  float maxforce;
  float maxspeed;
  float eatRange;
  int framesSinceFood;
  PShape image;
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
    //tintHue    = int(random(215, 235));
    image      = loadShape("prey.svg");
    drawWidth  = 7; //<>//
    drawHeight = 7;
    margin = 7;
    acceleration = new PVector(0, 0);
    // sets the initial velocity of the prey
    float angle = random(TWO_PI);
    velocity = new PVector(cos(angle), sin(angle));
    location = new PVector();
    maxspeed = 6;
    maxforce = 0.09;
    eatRange = 5.0;
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
  boolean dying()
  {
    // number of frames that it takes for the prey to die
    if (framesSinceFood > 900)
    {
      return true;
    }
    return false;
  }

  // moves, draws, and feeds the prey
  void Update(ArrayList<Prey> preys, ArrayList<Plankton> planktons) {
    flock(preys);
    move();
    borders();
    render();
    eat(planktons);

    framesSinceFood++;
  }

  void applyForce(PVector force) {
    acceleration.add(force);
  }

  // Sets acceleration
  // We accumulate a new acceleration each time based on three rules
  void flock(ArrayList<Prey> preys) {
    // Seperation
    PVector sep = separate(preys);
    // Alignment
    PVector ali = align(preys);
    // Cohesion
    PVector coh = cohesion(preys);
    // Arbitrarily weight these forces
    sep.mult(2.5);
    ali.mult(3);
    coh.mult(1);
    // Add the force vectors to acceleration
    applyForce(sep);
    applyForce(ali);
    applyForce(coh);
  }

  // Method to update location
  void move() {
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
  PVector seek(PVector target) {
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

  void render() {
    // Draw an organism rotated in the direction of velocity
    float theta = velocity.heading() + radians(90);
    
    colorMode(RGB);  
    // noStroke();
    // triangle((location.x), (location.y+(drawHeight/2)), (location.x-(drawWidth/2)), (location.y-(drawHeight/2)), (location.x+(drawWidth/2)), (location.y-(drawHeight/2)));
    // rotate(1);
    shape(image, location.x, location.y, 10, 10);
    fill(40, 30, 190);
    pushMatrix();
    rotate(theta);
    popMatrix();
  }

  // Wraparound
  void borders() {
    if (location.x < -margin) location.x = width+margin;
    if (location.y < -margin) location.y = height+margin;
    if (location.x > width+margin) location.x = -margin;
    if (location.y > height+margin) location.y = -margin;
  }

  ///////////////////////////////////////////////////////////////////////////////////////// DONT CARE
  /////// this has not been altered from the generic flocking code from processing.org

  // Separation
  // Method checks for nearby preys and steers away
  PVector separate (ArrayList<Prey> preys) {
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
  PVector align (ArrayList<Prey> preys) {
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
  PVector cohesion (ArrayList<Prey> preys) {
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
