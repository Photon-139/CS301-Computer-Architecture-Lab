	.data
a:
	100
	.text
main:
    load %x0, $a, %x3
    add %x0, %x3, %x4
    add %x0, %x0, %x5
    jmp loop
loop:
    beq %x4, %x0, check
    divi %x4, 10, %x4
    muli %x5, 10, %x5
    add %x5, %x31, %x5
    jmp loop
check:
    beq %x3, %x5, palindrome
    subi %x0, 1, %x10
    end
palindrome:
    addi %x0, 1, %x10
    end