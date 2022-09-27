class Consumer { //<>// //<>//

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
  void Initialize() {
    location   = new PVector(random(-margin, width + margin), random(-margin, height + margin));
    tintHue    = int(random(215, 235));
    image      = loadShape("consumer.svg");
    drawWidth  = 7;
    drawHeight = 7;
    margin = 2;
    moveRange  = 3;
    velocity = new PVector(5, 0);
    maxforce = 1.0;
    maxspeed = 10.0; 
    eatRange = 6.0;
    framesSinceFood = 0;
    foodLagTimer = 0;
  }

  void Update(ArrayList<Algae> algaes, ArrayList<Consumer> consumers) 
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
  void render() {
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
  void move(ArrayList<Algae> algaes) {

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
  void wrapAround() {
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
  boolean dying()
  {
    //number of frames that it takes for the prey to die
    if (framesSinceFood > 600)
    {
      return true;
    }
    return false;
  }

  //finds the closest prey  
  Algae findClosestAlgae(ArrayList<Algae> algaes) {

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
  PVector findClosestCircle(Algae algae)
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

  PVector getAcceleration(PVector destination) {
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

  void chasePredator(Predator t)
  {
    this.target = t;
    eatStage = 3;
    hasTarget = true;
  }

  // gets the closest consumers
  void recruitClosestConsumers(ArrayList<Consumer> consumers)
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
