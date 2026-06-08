package ua.edu.ukma;

@GenerateDto
public class AccountEntity {
    
    public Long id;

    @MinLength(3)
    public String username;
    
    @MinLength(8)
    @ExcludeFromDto
    public String password;

    @MaxValue(120)
    public int age;
    
    public String email;
}