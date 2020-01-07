<?php

class C1
{
    protected function foo() { return; }    // NOK {{Use of protected class members is discouraged.}}

    private static function i() { return; }  // OK
    protected abstract function bar() { return; }         // OK
    public function k() { return; }          // OK

    protected $myVariable;  // NOK {{Use of protected class members is discouraged.}}
    public $myVariable;      // OK
    private static $myVariable;      // OK
    private $myVariable;      // OK
}

