package com.example.battlebooks.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Role implements Serializable  
{
   ROLE_STUDENT("ROLE_STUDENT", "Student"), 
   ROLE_ADMIN("ROLE_ADMIN", "Admin");

   private static final long serialVersionUID = -3552817198903379891L;

   private String authority;
   private String description;

   private Role (String authority, String description)
   {
      this.authority = authority;
      this.description = description;
   }

   private Role () {}

   public String getAuthority () {
      return this.authority;
   }

   public String toString () {
      return description;
   }

   public static List<Role> getEffectiveRoles () {
     return  new ArrayList<> (Arrays.asList (Role.values()));
   }
}