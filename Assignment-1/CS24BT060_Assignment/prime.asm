	.data
a:
	17
	.text
main:
    load %x0, $a, %x3
    addi %x0, 2, %x4
    jmp loop
loop:
    beq %x3, %x4, prime
    div %x3, %x4, %x5
    beq %x31, %x0, notprime
    addi %x4, 1, %x4
    jmp loop
prime:
    addi %x0, 1, %x10
    end
notprime:
    subi %x0, 1, %x10
    end