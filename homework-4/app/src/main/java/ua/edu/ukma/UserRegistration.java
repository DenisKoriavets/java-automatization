package ua.edu.ukma;

@GenerateDto
public class UserRegistration {
    
    @MinLength(3)
    public String username;
    
    @MinLength(8)
    public String password;
}