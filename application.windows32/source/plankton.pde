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
    tintHue    = int(random(0, 250));
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
