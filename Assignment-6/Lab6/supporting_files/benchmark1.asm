.text
main:
    addi %x0, 10000, %x3
    addi %x0, 1, %x4
loop:
    beq %x3, %x0, end
    add %x5, %x4, %x5
    add %x6, %x4, %x6
    add %x7, %x4, %x7
    add %x8, %x4, %x8
    add %x9, %x4, %x9
    add %x10, %x4, %x10
    sub %x3, %x4, %x3
    jmp loop
end:
    end