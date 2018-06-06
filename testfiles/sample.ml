//sample mini language file
//this is how program shaped
//note that /**/ formed comments are NOT allowed

declaration: x, y, i, j //declaration of global variables
fun x(x,y) //function declaration
{
    declaration: x1, x2 //declaration of function variables
    x1 := 3
    return x + y + x1
}

fun fibonacci(x)
{
    if
        x <= 1 -> return 1
    |   x > 1 -> return fibonacci(x - 1) + fibonacci(x - 2) //recursive call in IF statement
    fi
}

fun sort(x, y, z, w)
{
    do
        x > y -> x, y := y, x
    |   y > z -> y, z := z, y
    |   z > w -> z, w := w, z
    od
    //return x, y, z, w // not sure if array types will be supported
}

x := random(10)
print(x)

if
    x(1, 2) > 3 -> x := 4
|   1 < 2 -> x := 2
fi