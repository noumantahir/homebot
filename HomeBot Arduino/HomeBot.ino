/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.
 
  This example code is in the public domain.
 */
 #include <Servo.h> 
 
 
 
// Pin 13 has an LED connected on most Arduino boards.
// give it a name:
int led = 13;
int pinLeftF = 3;
int pinLeftR = 5;
int pinRightF = 6;
int pinRightR = 9;

int right_speed = 255;
Servo myservo;

// the setup routine runs once when you press reset:
void setup() {     
  Serial.begin(9600);  
  // initialize the digital pin as an output.
  pinMode(led, OUTPUT);
  pinMode(pinLeftF, OUTPUT);
  pinMode(pinLeftR, OUTPUT);
  pinMode(pinRightF, OUTPUT); 
  pinMode(pinRightR, OUTPUT);
  
  pinMode(13, OUTPUT);
  digitalWrite(13, HIGH);
  
  digitalWrite(pinLeftF, LOW);
  digitalWrite(pinLeftR, LOW);
  digitalWrite(pinRightF, LOW);
  digitalWrite(pinRightR, LOW);
  
  //forwardDrive();
  //reverseDrive();
  
  myservo.attach(10);
  
  myservo.write(90);
 
    
}

// the loop routine runs over and over again forever:
void loop() {
  
  if (Serial.available() > 0) {
      byte command = Serial.read();
      
      if(command == 1){
        forwardDrive();
      }
      
      else if(command == 0){
        stopRobot();
      }
      else if(command == 2){
        reverseDrive();
      }
      else if(command == 3){
        spinCounterClockwise();
      }
      else if(command == 4){
        spinClockwise();
      }
      
      else if (command >= 20 && command < 200){
        byte angle = command - 20;
        myservo.write(angle);
      }
  }
  delay(100);
}

void forwardDrive(){
  digitalWrite(pinLeftF, HIGH);
  digitalWrite(pinRightF, HIGH);
  
  digitalWrite(pinLeftR, LOW);
  analogWrite(pinRightR, 0);
}

void reverseDrive(){
   digitalWrite(pinLeftF, LOW);
  analogWrite(pinRightF, 0);
  digitalWrite(pinLeftR, HIGH);
  analogWrite(pinRightR, right_speed);
}

void stopRobot(){
    digitalWrite(pinLeftF, LOW);
  analogWrite(pinRightF, 0);
  digitalWrite(pinLeftR, LOW);
  analogWrite(pinRightR, 0);
}

void spinCounterClockwise(){
  digitalWrite(pinLeftF, LOW);
  analogWrite(pinRightF, right_speed);
  digitalWrite(pinLeftR, HIGH);
  analogWrite(pinRightR, 0);
}

void spinClockwise(){
    digitalWrite(pinLeftF, HIGH);
  analogWrite(pinRightF, 0);
  digitalWrite(pinLeftR, LOW);
  analogWrite(pinRightR, right_speed);
}
