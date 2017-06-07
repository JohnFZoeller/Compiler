 import java.io.*;
 import java.util.*;

 public class UndefinedTypeError extends Error{
 	public UndefinedTypeError(String badType){
 		super("Reference of " + badType + " has not been defined");
 	}
 }