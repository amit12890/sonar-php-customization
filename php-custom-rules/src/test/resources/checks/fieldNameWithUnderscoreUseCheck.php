<?php

class myClass {

  public $_myVariable;  // NOK {{Property name "$_myVariable" should not be prefixed with an underscore to indicate visibility}}
  public $myVariable;      // OK
  private $myVariable;      // OK

}
